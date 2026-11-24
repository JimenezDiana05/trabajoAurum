(() => {
  const menu = document.querySelector(".site-menu");
  const menuBackdrop = document.querySelector(".menu-backdrop");
  const menuToggle = document.querySelector(".menu-toggle");
  const menuClose = document.querySelector(".menu-close");
  const cart = document.querySelector(".cart-panel");
  const cartBackdrop = document.querySelector(".cart-backdrop");
  const cartItemsElement = document.querySelector(".cart-items");
  const cartEmpty = document.querySelector(".cart-empty");
  const cartSummary = document.querySelector(".cart-summary");
  const toast = document.querySelector(".toast");
  const orderDialog = document.querySelector(".order-dialog");

  if (!menu || !cart || !menuToggle || !cartItemsElement) return;

  const money = new Intl.NumberFormat("es-MX", {
    style: "currency",
    currency: "MXN",
    maximumFractionDigits: 0
  });
  const storageKey = "aurum-cart";
  let items = loadCart();
  let toastTimer;

  function loadCart() {
    try {
      const saved = JSON.parse(localStorage.getItem(storageKey) || "[]");
      if (!Array.isArray(saved)) return [];
      return saved.filter(item =>
        item && typeof item.id === "string" &&
        typeof item.name === "string" && Number.isFinite(item.price) &&
        Number.isInteger(item.quantity) && item.quantity > 0
      );
    } catch (error) {
      console.error("No se pudo recuperar el carrito guardado.", error);
      return [];
    }
  }

  function saveCart() {
    try {
      localStorage.setItem(storageKey, JSON.stringify(items));
    } catch (error) {
      console.error("No se pudo guardar el carrito.", error);
      showToast("No se pudo guardar el carrito en este dispositivo.");
    }
  }

  function setScrollLock(isLocked) {
    document.body.classList.toggle("no-scroll", isLocked);
  }

  function closeMenu() {
    menu.classList.remove("is-open");
    menu.setAttribute("aria-hidden", "true");
    menuToggle.setAttribute("aria-expanded", "false");
    menuBackdrop.hidden = true;
    setScrollLock(cart.classList.contains("is-open"));
  }

  function openMenu() {
    closeCart();
    menu.classList.add("is-open");
    menu.setAttribute("aria-hidden", "false");
    menuToggle.setAttribute("aria-expanded", "true");
    menuBackdrop.hidden = false;
    setScrollLock(true);
    menuClose.focus();
  }

  function openCart() {
    closeMenu();
    cart.classList.add("is-open");
    cart.setAttribute("aria-hidden", "false");
    cartBackdrop.hidden = false;
    setScrollLock(true);
    document.querySelector(".cart-close").focus();
  }

  function closeCart() {
    cart.classList.remove("is-open");
    cart.setAttribute("aria-hidden", "true");
    cartBackdrop.hidden = true;
    setScrollLock(menu.classList.contains("is-open"));
  }

  function showToast(message) {
    toast.textContent = message;
    toast.classList.add("is-visible");
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => toast.classList.remove("is-visible"), 2600);
  }

  function renderCart() {
    const quantity = items.reduce((total, item) => total + item.quantity, 0);
    const subtotal = items.reduce((total, item) => total + item.price * item.quantity, 0);
    document.querySelector(".cart-count").textContent = quantity;
    document.querySelector(".menu-cart-count").textContent = quantity;
    document.querySelector(".cart-panel-count").textContent = `(${quantity})`;
    cartEmpty.hidden = items.length > 0;
    cartSummary.hidden = items.length === 0;
    document.querySelector(".cart-subtotal").textContent = money.format(subtotal);
    document.querySelectorAll(".add-to-cart").forEach(button => {
      const inCart = items.some(item => item.id === button.dataset.id);
      button.textContent = inCart ? "En la bolsa" : "Agregar";
      button.setAttribute("aria-label", `${inCart ? "Añadir otra unidad de" : "Agregar"} ${button.dataset.name} a la bolsa`);
    });
    cartItemsElement.replaceChildren();

    items.forEach(item => {
      const row = document.createElement("article");
      row.className = "cart-item";
      row.innerHTML = `
        <div>
          <h3>${escapeHTML(item.name)}</h3>
          <p>${money.format(item.price)}</p>
          <div class="cart-item-actions">
            <button class="quantity-button" type="button" data-action="decrease" data-id="${escapeHTML(item.id)}" aria-label="Quitar una unidad de ${escapeHTML(item.name)}">−</button>
            <span>${item.quantity}</span>
            <button class="quantity-button" type="button" data-action="increase" data-id="${escapeHTML(item.id)}" aria-label="Añadir una unidad de ${escapeHTML(item.name)}">+</button>
            <button class="remove-item" type="button" data-action="remove" data-id="${escapeHTML(item.id)}">Eliminar</button>
          </div>
        </div>
        <strong class="cart-item-total">${money.format(item.price * item.quantity)}</strong>
      `;
      cartItemsElement.append(row);
    });
  }

  function escapeHTML(value) {
    return value.replace(/[&<>"']/g, character => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#39;"
    })[character]);
  }

  menuToggle.addEventListener("click", openMenu);
  menuClose.addEventListener("click", closeMenu);
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

  document.querySelectorAll(".favorite-button").forEach(button => {
    button.addEventListener("click", () => {
      const isFavorite = button.getAttribute("aria-pressed") !== "true";
      button.setAttribute("aria-pressed", String(isFavorite));
      button.textContent = isFavorite ? "♥" : "♡";
      showToast(isFavorite ? "Añadido a tus favoritos." : "Eliminado de tus favoritos.");
    });
  });

  document.querySelectorAll(".add-to-cart").forEach(button => {
    button.addEventListener("click", () => {
      const product = {
        id: button.dataset.id,
        name: button.dataset.name,
        price: Number(button.dataset.price)
      };
      const existing = items.find(item => item.id === product.id);
      if (existing) existing.quantity += 1;
      else items.push({ ...product, quantity: 1 });
      saveCart();
      renderCart();
      showToast(`${product.name} se añadió a tu bolsa.`);
    });
  });

  cartItemsElement.addEventListener("click", event => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;
    const item = items.find(entry => entry.id === button.dataset.id);
    if (!item) return;
    if (button.dataset.action === "remove" || (button.dataset.action === "decrease" && item.quantity === 1)) {
      items = items.filter(entry => entry.id !== item.id);
    } else if (button.dataset.action === "decrease") {
      item.quantity -= 1;
    } else if (button.dataset.action === "increase") {
      item.quantity += 1;
    }
    saveCart();
    renderCart();
  });

  document.querySelector(".checkout-button").addEventListener("click", () => {
    if (items.length === 0) return;
    const orderNumber = `AR-${new Date().getFullYear()}-${String(Date.now()).slice(-6)}`;
    items = [];
    saveCart();
    renderCart();
    closeCart();
    const message = document.querySelector(".order-message");
    message.textContent = `¡Gracias! Tu pedido de demostración ${orderNumber} quedó registrado.`;
    document.querySelector("#order-number").value = orderNumber;
    orderDialog.showModal();
    showToast("Compra de demostración completada.");
  });

  document.querySelector(".order-form").addEventListener("submit", event => {
    event.preventDefault();
    const input = document.querySelector("#order-number");
    const message = document.querySelector(".order-message");
    const number = input.value.trim();
    if (!number) {
      input.focus();
      message.textContent = "Escribe un número de pedido para realizar la consulta.";
      return;
    }
    message.textContent = `Pedido ${number}: consulta de demostración. Si realizaste una compra, recibirás actualizaciones por correo.`;
  });

  document.addEventListener("keydown", event => {
    if (event.key === "Escape") {
      closeMenu();
      closeCart();
    }
  });

  const slides = [...document.querySelectorAll(".hero-slide")];
  const dots = [...document.querySelectorAll(".slide-dot")];
  const slideNumber = document.querySelector(".current-slide");
  let activeSlide = 0;
  let slideTimer;

  function showSlide(index) {
    activeSlide = (index + slides.length) % slides.length;
    slides.forEach((slide, position) => {
      const isActive = position === activeSlide;
      slide.classList.toggle("is-active", isActive);
      slide.setAttribute("aria-hidden", String(!isActive));
      dots[position].classList.toggle("is-active", isActive);
      dots[position].setAttribute("aria-current", String(isActive));
    });
    slideNumber.textContent = String(activeSlide + 1).padStart(2, "0");
  }

  function restartSlideTimer() {
    window.clearInterval(slideTimer);
    slideTimer = window.setInterval(() => showSlide(activeSlide + 1), 6000);
  }

  document.querySelector(".previous-slide").addEventListener("click", () => {
    showSlide(activeSlide - 1);
    restartSlideTimer();
  });
  document.querySelector(".next-slide").addEventListener("click", () => {
    showSlide(activeSlide + 1);
    restartSlideTimer();
  });
  dots.forEach((dot, index) => dot.addEventListener("click", () => {
    showSlide(index);
    restartSlideTimer();
  }));
  document.querySelector(".hero").addEventListener("mouseenter", () => window.clearInterval(slideTimer));
  document.querySelector(".hero").addEventListener("mouseleave", restartSlideTimer);

  renderCart();
  restartSlideTimer();
})();
