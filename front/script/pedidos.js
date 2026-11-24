(() => {
  const menu = document.querySelector(".site-menu");
  const backdrop = document.querySelector(".menu-backdrop");
  const toggle = document.querySelector(".menu-toggle");
  const close = document.querySelector(".menu-close");

  function setMenu(open) {
    menu.classList.toggle("is-open", open);
    menu.setAttribute("aria-hidden", String(!open));
    toggle.setAttribute("aria-expanded", String(open));
    backdrop.hidden = !open;
    document.body.classList.toggle("no-scroll", open);
  }
  toggle?.addEventListener("click", () => setMenu(true));
  close?.addEventListener("click", () => setMenu(false));
  backdrop?.addEventListener("click", () => setMenu(false));

  const cartKey = "aurum-cart";
  let items = [];
  try {
    const stored = JSON.parse(localStorage.getItem(cartKey) || "[]");
    if (Array.isArray(stored)) items = stored;
  } catch (error) {
    console.error("No se pudo leer el carrito.", error);
  }
  const count = items.reduce((total, item) => total + (item.quantity || 0), 0);
  document.querySelectorAll(".cart-count").forEach(element => {
    element.textContent = count;
  });

  if (document.querySelector(".order-tabs")) {
    const isDelivered = new URLSearchParams(window.location.search).get("estado") === "entregados";
    const tabs = [...document.querySelectorAll(".order-tab")];
    const activeOrders = document.querySelector(".active-orders");
    const deliveredOrders = document.querySelector(".delivered-orders");

    tabs.forEach((tab, index) => tab.classList.toggle("is-active", isDelivered ? index === 1 : index === 0));
    if (activeOrders) activeOrders.hidden = isDelivered;
    if (deliveredOrders) deliveredOrders.hidden = !isDelivered;
  }

  if (document.body.dataset.page === "cart") {
    const cards = [...document.querySelectorAll(".cart-card")];
    const selectedCount = document.querySelector(".selected-count");

    function updateSummary() {
      const total = cards.reduce((sum, card) => card.isConnected ? sum + Number(card.dataset.price) : sum, 0);
      document.querySelector("#subtotal").textContent = `$${total.toLocaleString("es-MX")}`;
      document.querySelector("#total").textContent = `$${total.toLocaleString("es-MX")}`;
      const remaining = cards.filter(card => card.isConnected).length;
      selectedCount.textContent = `${remaining} ${remaining === 1 ? "pieza seleccionada" : "piezas seleccionadas"}`;
      document.querySelector(".cart-count").textContent = remaining;
    }

    document.querySelectorAll(".remove-button").forEach(button => {
      button.addEventListener("click", () => {
        button.closest(".cart-card").remove();
        updateSummary();
      });
    });

    document.querySelector(".checkout-button")?.addEventListener("click", () => {
      alert("El pago de demostración no ha sido configurado.");
    });
  }
})();
