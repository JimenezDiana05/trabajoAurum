import { Component, AfterViewInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router'; // <-- 1. Agrega esta importación

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [RouterLink], // <-- 2. Agrega esta línea aquí
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
  encapsulation: ViewEncapsulation.None
})
export class Inicio implements AfterViewInit, OnDestroy {
  private toastTimer?: number;
  private slideTimer?: number;

  ngAfterViewInit(): void {
    if (typeof document === 'undefined') return;

    const accountToggle = document.querySelector<HTMLButtonElement>('.account-link');
    const accountDropdown = document.querySelector<HTMLElement>('.account-dropdown');
    const setAccountMenu = (open: boolean) => {
      if (!accountToggle || !accountDropdown) return;
      accountDropdown.hidden = !open;
      accountToggle.setAttribute('aria-expanded', String(open));
    };
    accountToggle?.addEventListener('click', event => {
      event.stopPropagation();
      setAccountMenu(accountDropdown?.hidden !== false);
    });
    accountDropdown?.querySelectorAll('a').forEach(link =>
      link.addEventListener('click', () => setAccountMenu(false))
    );
    document.addEventListener('click', event => {
      if (!accountDropdown?.contains(event.target as Node) &&
          !accountToggle?.contains(event.target as Node)) {
        setAccountMenu(false);
      }
    });

    const menu = document.querySelector<HTMLElement>('.site-menu');
    const menuBackdrop = document.querySelector<HTMLElement>('.menu-backdrop');
    const menuToggle = document.querySelector<HTMLElement>('.menu-toggle');
    const menuClose = document.querySelector<HTMLElement>('.menu-close');
    const cart = document.querySelector<HTMLElement>('.cart-panel');
    const cartBackdrop = document.querySelector<HTMLElement>('.cart-backdrop');
    const cartItemsElement = document.querySelector<HTMLElement>('.cart-items');
    const cartEmpty = document.querySelector<HTMLElement>('.cart-empty');
    const cartSummary = document.querySelector<HTMLElement>('.cart-summary');
    const toast = document.querySelector<HTMLElement>('.toast');
    const orderDialog = document.querySelector<HTMLDialogElement>('.order-dialog');

    if (!menu || !cart || !menuToggle || !cartItemsElement) return;

    const money = new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
      maximumFractionDigits: 0
    });
    const storageKey = 'aurum-cart';

    const loadCart = (): CartItem[] => {
      try {
        const saved = JSON.parse(localStorage.getItem(storageKey) || '[]');
        if (!Array.isArray(saved)) return [];
        return saved.filter(
          (item: any): item is CartItem =>
            Boolean(item) &&
            typeof item.id === 'string' &&
            typeof item.name === 'string' &&
            Number.isFinite(item.price) &&
            Number.isInteger(item.quantity) &&
            item.quantity > 0
        );
      } catch (error) {
        console.error('No se pudo recuperar el carrito guardado.', error);
        return [];
      }
    };

    let items: CartItem[] = loadCart();

    const showToast = (message: string) => {
      if (!toast) return;
      toast.textContent = message;
      toast.classList.add('is-visible');
      window.clearTimeout(this.toastTimer);
      this.toastTimer = window.setTimeout(() => toast.classList.remove('is-visible'), 2600);
    };

    const saveCart = () => {
      try {
        localStorage.setItem(storageKey, JSON.stringify(items));
      } catch (error) {
        console.error('No se pudo guardar el carrito.', error);
        showToast('No se pudo guardar el carrito en este dispositivo.');
      }
    };

    const setScrollLock = (isLocked: boolean) => {
      document.body.classList.toggle('no-scroll', isLocked);
    };

    const closeMenu = () => {
      menu.classList.remove('is-open');
      menu.setAttribute('aria-hidden', 'true');
      menuToggle.setAttribute('aria-expanded', 'false');
      if (menuBackdrop) menuBackdrop.hidden = true;
      setScrollLock(cart.classList.contains('is-open'));
    };

    const closeCart = () => {
      cart.classList.remove('is-open');
      cart.setAttribute('aria-hidden', 'true');
      if (cartBackdrop) cartBackdrop.hidden = true;
      setScrollLock(menu.classList.contains('is-open'));
    };

    const openMenu = () => {
      closeCart();
      menu.classList.add('is-open');
      menu.setAttribute('aria-hidden', 'false');
      menuToggle.setAttribute('aria-expanded', 'true');
      if (menuBackdrop) menuBackdrop.hidden = false;
      setScrollLock(true);
      menuClose?.focus();
    };

    const openCart = (event?: Event) => {
      if (event) event.preventDefault();
      closeMenu();
      cart.classList.add('is-open');
      cart.setAttribute('aria-hidden', 'false');
      if (cartBackdrop) cartBackdrop.hidden = false;
      setScrollLock(true);
      document.querySelector<HTMLElement>('.cart-close')?.focus();
    };

    const escapeHTML = (value: string) =>
      value.replace(/[&<>"']/g, character => {
        const map: Record<string, string> = {
          '&': '&amp;',
          '<': '&lt;',
          '>': '&gt;',
          '"': '&quot;',
          "'": '&#39;'
        };
        return map[character] || character;
      });

    const renderCart = () => {
      const quantity = items.reduce((total, item) => total + item.quantity, 0);
      const subtotal = items.reduce((total, item) => total + item.price * item.quantity, 0);

      const cartCount = document.querySelector('.cart-count');
      const menuCartCount = document.querySelector('.menu-cart-count');
      const cartPanelCount = document.querySelector('.cart-panel-count');
      const cartSubtotal = document.querySelector('.cart-subtotal');

      if (cartCount) cartCount.textContent = String(quantity);
      if (menuCartCount) menuCartCount.textContent = String(quantity);
      if (cartPanelCount) cartPanelCount.textContent = `(${quantity})`;
      if (cartEmpty) cartEmpty.hidden = items.length > 0;
      if (cartSummary) cartSummary.hidden = items.length === 0;
      if (cartSubtotal) cartSubtotal.textContent = money.format(subtotal);

      document.querySelectorAll<HTMLButtonElement>('.add-to-cart').forEach(button => {
        const inCart = items.some(item => item.id === button.dataset['id']);
        button.textContent = inCart ? 'En la bolsa' : 'Agregar';
        button.setAttribute(
          'aria-label',
          `${inCart ? 'Añadir otra unidad de' : 'Agregar'} ${button.dataset['name']} a la bolsa`
        );
      });

      cartItemsElement.replaceChildren();

      items.forEach(item => {
        const row = document.createElement('article');
        row.className = 'cart-item';
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
    };

    menuToggle.addEventListener('click', openMenu);
    menuClose?.addEventListener('click', closeMenu);
    menuBackdrop?.addEventListener('click', closeMenu);
    document.querySelectorAll('.menu-links a').forEach(link => link.addEventListener('click', closeMenu));
    document.querySelector('.cart-close')?.addEventListener('click', closeCart);
    cartBackdrop?.addEventListener('click', closeCart);
    document.querySelector('.order-close')?.addEventListener('click', () => orderDialog?.close());
    orderDialog?.addEventListener('click', event => {
      if (event.target === orderDialog) orderDialog.close();
    });
    document.querySelector('.back-to-top')?.addEventListener('click', closeMenu);

    document.querySelectorAll<HTMLButtonElement>('.favorite-button').forEach(button => {
      button.addEventListener('click', () => {
        const isFavorite = button.getAttribute('aria-pressed') !== 'true';
        button.setAttribute('aria-pressed', String(isFavorite));
        button.textContent = isFavorite ? '♥' : '♡';
        showToast(isFavorite ? 'Añadido a tus favoritos.' : 'Eliminado de tus favoritos.');
      });
    });

    document.querySelectorAll<HTMLButtonElement>('.add-to-cart').forEach(button => {
      button.addEventListener('click', () => {
        const product = {
          id: button.dataset['id'] || '',
          name: button.dataset['name'] || '',
          price: Number(button.dataset['price'] || 0)
        };
        const existing = items.find(item => item.id === product.id);
        if (existing) existing.quantity += 1;
        else items.push({ ...product, quantity: 1 });
        saveCart();
        renderCart();
        showToast(`${product.name} se añadió a tu bolsa.`);
      });
    });

    cartItemsElement.addEventListener('click', event => {
      const target = event.target as HTMLElement;
      const button = target.closest<HTMLButtonElement>('button[data-action]');
      if (!button) return;
      const item = items.find(entry => entry.id === button.dataset['id']);
      if (!item) return;
      const action = button.dataset['action'];
      if (action === 'remove' || (action === 'decrease' && item.quantity === 1)) {
        items = items.filter(entry => entry.id !== item.id);
      } else if (action === 'decrease') {
        item.quantity -= 1;
      } else if (action === 'increase') {
        item.quantity += 1;
      }
      saveCart();
      renderCart();
    });

    document.querySelector('.checkout-button')?.addEventListener('click', () => {
      if (items.length === 0) return;
      const orderNumber = `AR-${new Date().getFullYear()}-${String(Date.now()).slice(-6)}`;
      items = [];
      saveCart();
      renderCart();
      closeCart();
      const message = document.querySelector('.order-message');
      const orderInput = document.querySelector<HTMLInputElement>('#order-number');
      if (message) {
        message.textContent = `¡Gracias! Tu pedido de demostración ${orderNumber} quedó registrado.`;
      }
      if (orderInput) orderInput.value = orderNumber;
      orderDialog?.showModal();
      showToast('Compra de demostración completada.');
    });

    document.querySelector('.order-form')?.addEventListener('submit', event => {
      event.preventDefault();
      const input = document.querySelector<HTMLInputElement>('#order-number');
      const message = document.querySelector('.order-message');
      if (!input || !message) return;
      const number = input.value.trim();
      if (!number) {
        input.focus();
        message.textContent = 'Escribe un número de pedido para realizar la consulta.';
        return;
      }
      message.textContent = `Pedido ${number}: consulta de demostración. Si realizaste una compra, recibirás actualizaciones por correo.`;
    });

    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') {
        closeMenu();
        closeCart();
      }
    });

    const slides = [...document.querySelectorAll<HTMLElement>('.hero-slide')];
    const dots = [...document.querySelectorAll<HTMLElement>('.slide-dot')];
    const slideNumber = document.querySelector<HTMLElement>('.current-slide');
    let activeSlide = 0;

    const showSlide = (index: number) => {
      if (slides.length === 0) return;
      activeSlide = (index + slides.length) % slides.length;
      slides.forEach((slide, position) => {
        const isActive = position === activeSlide;
        slide.classList.toggle('is-active', isActive);
        slide.setAttribute('aria-hidden', String(!isActive));
        dots[position]?.classList.toggle('is-active', isActive);
        dots[position]?.setAttribute('aria-current', String(isActive));
      });
      if (slideNumber) {
        slideNumber.textContent = String(activeSlide + 1).padStart(2, '0');
      }
    };

    const restartSlideTimer = () => {
      window.clearInterval(this.slideTimer);
      this.slideTimer = window.setInterval(() => showSlide(activeSlide + 1), 6000);
    };

    document.querySelector('.previous-slide')?.addEventListener('click', () => {
      showSlide(activeSlide - 1);
      restartSlideTimer();
    });
    document.querySelector('.next-slide')?.addEventListener('click', () => {
      showSlide(activeSlide + 1);
      restartSlideTimer();
    });
    dots.forEach((dot, index) =>
      dot.addEventListener('click', () => {
        showSlide(index);
        restartSlideTimer();
      })
    );
    document.querySelector('.hero')?.addEventListener('mouseenter', () => window.clearInterval(this.slideTimer));
    document.querySelector('.hero')?.addEventListener('mouseleave', restartSlideTimer);

    renderCart();
    restartSlideTimer();
  }

  ngOnDestroy(): void {
    if (typeof window === 'undefined') return;

    window.clearTimeout(this.toastTimer);
    window.clearInterval(this.slideTimer);
  }
}