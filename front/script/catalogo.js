(() => {
  const menu = document.querySelector(".site-menu");
  const menuBackdrop = document.querySelector(".menu-backdrop");
  const menuToggle = document.querySelector(".menu-toggle");
  const cart = document.querySelector(".cart-panel");
  const cartBackdrop = document.querySelector(".cart-backdrop");
  const cartItemsElement = document.querySelector(".cart-items");
  const cartEmpty = document.querySelector(".cart-empty");
  const cartSummary = document.querySelector(".cart-summary");
  const toast = document.querySelector(".toast");
  const orderDialog = document.querySelector(".order-dialog");
  const products = [...document.querySelectorAll(".catalog-card")];
  const tabs = [...document.querySelectorAll(".category-tab")];
  const count = document.querySelector(".result-count");
  const noResults = document.querySelector(".no-results");

  if (!menu || !cart || !menuToggle || products.length === 0) return;

  const money = new Intl.NumberFormat("es-MX", {
    style: "currency",
    currency: "MXN",
    maximumFractionDigits: 0
  });
  const cartKey = "aurum-cart";
  const favoritesKey = "aurum-favorites";
  let items = readStoredArray(cartKey).filter(item =>
    item && typeof item.id === "string" && typeof item.name === "string" &&
    Number.isFinite(item.price) && Number.isInteger(item.quantity) && item.quantity > 0
  );
  let favorites = readStoredArray(favoritesKey).filter(id => typeof id === "string");
  let toastTimer;
  let activeCategory = new URLSearchParams(window.location.search).get("categoria") || "todos";

  if (!tabs.some(tab => tab.dataset.category === activeCategory)) activeCategory = "todos";

  function readStoredArray(key) {
    try {
      const value = JSON.parse(localStorage.getItem(key) || "[]");
      return Array.isArray(value) ? value : [];
    } catch (error) {
      console.error(`No se pudo leer ${key}.`, error);
      return [];
    }
  }

  function save(key, value) {
    try {
      localStorage.setItem(key, JSON.stringify(value));
    } catch (error) {
      console.error(`No se pudo guardar ${key}.`, error);
      showToast("No se pudo guardar el cambio en este dispositivo.");
    }
  }

  function showToast(message) {
    toast.textContent = message;
    toast.classList.add("is-visible");
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => toast.classList.remove("is-visible"), 2600);
  }

  function setScrollLock(locked) {
    document.body.classList.toggle("no-scroll", locked);
  }

  function closeMenu() {
    menu.classList.remove("is-open");
    menu.setAttribute("aria-hidden", "true");
    menuToggle.setAttribute("aria-expanded", "false");
    menuBackdrop.hidden = true;
    setScrollLock(cart.classList.contains("is-open"));
  }

  function closeCart() {
    cart.classList.remove("is-open");
    cart.setAttribute("aria-hidden", "true");
    cartBackdrop.hidden = true;
    setScrollLock(menu.classList.contains("is-open"));
  }

  function openMenu() {
    closeCart();
    menu.classList.add("is-open");
    menu.setAttribute("aria-hidden", "false");
    menuToggle.setAttribute("aria-expanded", "true");
    menuBackdrop.hidden = false;
    setScrollLock(true);
    document.querySelector(".menu-close").focus();
  }

  function openCart() {
    closeMenu();
    cart.classList.add("is-open");
    cart.setAttribute("aria-hidden", "false");
    cartBackdrop.hidden = false;
    setScrollLock(true);
    document.querySelector(".cart-close").focus();
  }

  function renderCart() {
    const quantity = items.reduce((total, item) => total + item.quantity, 0);
    const subtotal = items.reduce((total, item) => total + item.price * item.quantity, 0);
    document.querySelector(".cart-count").textContent = quantity;
    document.querySelector(".menu-cart-count").textContent = quantity;
    document.querySelector(".cart-panel-count").textContent = `(${quantity})`;
    document.querySelector(".cart-subtotal").textContent = money.format(subtotal);
    cartEmpty.hidden = items.length > 0;
    cartSummary.hidden = items.length === 0;
    cartItemsElement.replaceChildren();

    document.querySelectorAll(".catalog-card .add-to-cart").forEach(button => {
      const product = button.closest(".catalog-card");
      const inCart = items.some(item => item.id === product.dataset.id);
      button.textContent = inCart ? "En la bolsa" : "Agregar";
      button.setAttribute("aria-label", `${inCart ? "Añadir otra unidad de" : "Agregar"} ${product.dataset.name} a la bolsa`);
    });

    items.forEach(item => {
      const row = document.createElement("article");
      row.className = "cart-item";
      const description = document.createElement("div");
      const name = document.createElement("h3");
      name.textContent = item.name;
      const price = document.createElement("p");
      price.textContent = money.format(item.price);
      const actions = document.createElement("div");
      actions.className = "cart-item-actions";

      for (const [action, label, text] of [
        ["decrease", `Quitar una unidad de ${item.name}`, "−"],
        ["increase", `Añadir una unidad de ${item.name}`, "+"]
      ]) {
        const button = document.createElement("button");
        button.className = "quantity-button";
        button.type = "button";
        button.dataset.action = action;
        button.dataset.id = item.id;
        button.setAttribute("aria-label", label);
        button.textContent = text;
        if (action === "decrease") actions.append(button);
        else {
          const amount = document.createElement("span");
          amount.textContent = item.quantity;
          actions.append(amount, button);
        }
      }

      const remove = document.createElement("button");
      remove.className = "remove-item";
      remove.type = "button";
      remove.dataset.action = "remove";
      remove.dataset.id = item.id;
      remove.textContent = "Eliminar";
      actions.append(remove);
      description.append(name, price, actions);

      const total = document.createElement("strong");
      total.className = "cart-item-total";
      total.textContent = money.format(item.price * item.quantity);
      row.append(description, total);
      cartItemsElement.append(row);
    });
  }

  function applyFilters() {
    const searchTerm = document.querySelector(".search-input").value.trim().toLocaleLowerCase("es");
    const selectedPrice = document.querySelector(".price-filter").value;
    const selectedBrand = document.querySelector(".brand-filter").value;
    const selectedMaterial = document.querySelector(".material-filter").value;
    let visibleCount = 0;

    products.forEach(product => {
      const price = Number(product.dataset.price);
      const matchesCategory = activeCategory === "todos" || product.dataset.category === activeCategory;
      const matchesSearch = `${product.dataset.name} ${product.dataset.brand} ${product.dataset.material}`
        .toLocaleLowerCase("es").includes(searchTerm);
      const matchesBrand = selectedBrand === "all" || product.dataset.brand === selectedBrand;
      const matchesMaterial = selectedMaterial === "all" || product.dataset.material === selectedMaterial;
      const matchesPrice = selectedPrice === "all" ||
        (selectedPrice === "under-10000" && price < 10000) ||
        (selectedPrice === "10000-25000" && price >= 10000 && price <= 25000) ||
        (selectedPrice === "over-25000" && price > 25000);
      const isVisible = matchesCategory && matchesSearch && matchesBrand && matchesMaterial && matchesPrice;
      product.hidden = !isVisible;
      if (isVisible) visibleCount += 1;
    });

    count.textContent = `${visibleCount} ${visibleCount === 1 ? "pieza" : "piezas"}`;
    noResults.hidden = visibleCount > 0;
  }

  function setCategory(category, updateAddress = true) {
    activeCategory = category;
    tabs.forEach(tab => {
      const isActive = tab.dataset.category === category;
      tab.classList.toggle("is-active", isActive);
      tab.setAttribute("aria-pressed", String(isActive));
    });
    if (updateAddress) {
      const url = new URL(window.location.href);
      if (category === "todos") url.searchParams.delete("categoria");
      else url.searchParams.set("categoria", category);
      window.history.replaceState({}, "", url);
    }
    applyFilters();
  }

  menuToggle.addEventListener("click", openMenu);
  document.querySelector(".menu-close").addEventListener("click", closeMenu);
  menuBackdrop.addEventListener("click", closeMenu);
  document.querySelectorAll(".menu-links a").forEach(link => link.addEventListener("click", closeMenu));
  document.querySelector(".cart-toggle").addEventListener("click", openCart);
  document.querySelector(".cart-close").addEventListener("click", closeCart);
  cartBackdrop.addEventListener("click", closeCart);
  document.querySelector(".order-close").addEventListener("click", () => orderDialog.close());
  orderDialog.addEventListener("click", event => {
    if (event.target === orderDialog) orderDialog.close();
  });
  document.querySelector(".back-to-top").addEventListener("click", closeMenu);

  tabs.forEach(tab => tab.addEventListener("click", () => {
    setCategory(tab.dataset.category);
    document.querySelector("#productos").scrollIntoView({ behavior: "smooth" });
  }));
  document.querySelector(".search-input").addEventListener("input", applyFilters);
  document.querySelector(".price-filter").addEventListener("change", applyFilters);
  document.querySelector(".brand-filter").addEventListener("change", applyFilters);
  document.querySelector(".material-filter").addEventListener("change", applyFilters);

  document.querySelectorAll(".favorite-button").forEach(button => {
    const productId = button.closest(".catalog-card").dataset.id;
    const isFavorite = favorites.includes(productId);
    button.setAttribute("aria-pressed", String(isFavorite));
    button.textContent = isFavorite ? "♥" : "♡";
    button.addEventListener("click", () => {
      const favorite = button.getAttribute("aria-pressed") !== "true";
      button.setAttribute("aria-pressed", String(favorite));
      button.textContent = favorite ? "♥" : "♡";
      favorites = favorite
        ? [...new Set([...favorites, productId])]
        : favorites.filter(id => id !== productId);
      save(favoritesKey, favorites);
      showToast(favorite ? "Añadido a tus favoritos." : "Eliminado de tus favoritos.");
    });
  });

  document.querySelectorAll(".catalog-card .add-to-cart").forEach(button => {
    button.addEventListener("click", () => {
      const product = button.closest(".catalog-card");
      const existing = items.find(item => item.id === product.dataset.id);
      if (existing) existing.quantity += 1;
      else items.push({
        id: product.dataset.id,
        name: product.dataset.name,
        price: Number(product.dataset.price),
        quantity: 1
      });
      save(cartKey, items);
      renderCart();
      showToast(`${product.dataset.name} se añadió a tu bolsa.`);
    });
  });

  cartItemsElement.addEventListener("click", event => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const item = items.find(entry => entry.id === button.dataset.id);
    if (!item) return;
    if (button.dataset.action === "remove" || (button.dataset.action === "decrease" && item.quantity === 1)) {
      items = items.filter(entry => entry.id !== item.id);
    } else if (button.dataset.action === "decrease") item.quantity -= 1;
    else if (button.dataset.action === "increase") item.quantity += 1;
    save(cartKey, items);
    renderCart();
  });

  document.querySelector(".checkout-button").addEventListener("click", () => {
    if (items.length === 0) return;
    const orderNumber = `AR-${new Date().getFullYear()}-${String(Date.now()).slice(-6)}`;
    items = [];
    save(cartKey, items);
    renderCart();
    closeCart();
    document.querySelector("#order-number").value = orderNumber;
    document.querySelector(".order-message").textContent = `¡Gracias! Tu pedido de demostración ${orderNumber} quedó registrado.`;
    orderDialog.showModal();
  });

  document.querySelector(".order-form").addEventListener("submit", event => {
    event.preventDefault();
    const input = document.querySelector("#order-number");
    const number = input.value.trim();
    if (!number) {
      input.focus();
      return;
    }
    document.querySelector(".order-message").textContent =
      `Pedido ${number}: consulta de demostración. Si realizaste una compra, recibirás actualizaciones por correo.`;
  });

  document.addEventListener("keydown", event => {
    if (event.key === "Escape") {
      closeMenu();
      closeCart();
    }
  });

  document.querySelector(".back-to-top").addEventListener("click", () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  });

  renderCart();
  setCategory(activeCategory, false);
})();
