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

  const cards = [...document.querySelectorAll(".cart-card")];
  const selectedCount = document.querySelector(".selected-count");
  const cartCount = document.querySelector(".cart-count");

  function updateSummary() {
    const remainingCards = cards.filter(card => card.isConnected);
    const total = remainingCards.reduce((sum, card) => sum + Number(card.dataset.price), 0);
    const formattedTotal = `$${total.toLocaleString("es-MX")}`;

    document.querySelector("#subtotal").textContent = formattedTotal;
    document.querySelector("#total").textContent = formattedTotal;
    selectedCount.textContent = `${remainingCards.length} ${remainingCards.length === 1 ? "pieza seleccionada" : "piezas seleccionadas"}`;
    cartCount.textContent = remainingCards.length;
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

  updateSummary();
})();
