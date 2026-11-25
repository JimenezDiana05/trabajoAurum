import { Component, AfterViewInit, ViewEncapsulation, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  variant?: string;
  engraving?: string;
}

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './carrito.html',
  styleUrl: './carrito.css',
  encapsulation: ViewEncapsulation.None
})
export class Carrito implements AfterViewInit {
  private readonly router = inject(Router);

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
          !accountToggle?.contains(event.target as Node)) setAccountMenu(false);
    });

    const menu = document.querySelector<HTMLElement>('.site-menu');
    const backdrop = document.querySelector<HTMLElement>('.menu-backdrop');
    const toggle = document.querySelector<HTMLElement>('.menu-toggle');
    const close = document.querySelector<HTMLElement>('.menu-close');

    const setMenu = (open: boolean) => {
      if (!menu || !toggle || !backdrop) return;
      menu.classList.toggle('is-open', open);
      menu.setAttribute('aria-hidden', String(!open));
      toggle.setAttribute('aria-expanded', String(open));
      backdrop.hidden = !open;
      document.body.classList.toggle('no-scroll', open);
    };

    toggle?.addEventListener('click', () => setMenu(true));
    close?.addEventListener('click', () => setMenu(false));
    backdrop?.addEventListener('click', () => setMenu(false));
    document.querySelectorAll('.menu-links a').forEach(link =>
      link.addEventListener('click', () => setMenu(false))
    );

    const storageKey = 'aurum-cart';
    const cartList = document.querySelector<HTMLElement>('#cart-list');
    const selectedCount = document.querySelector<HTMLElement>('.selected-count');

    if (!cartList) return;

    const money = new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
      maximumFractionDigits: 0
    });

    const readCart = (): CartItem[] => {
      try {
        if (localStorage.getItem(storageKey) === null) {
          const initialCart: CartItem[] = [
            { id: 'eclat', name: 'Anillo Solitario Éclat', price: 32100, quantity: 1 }
          ];
          localStorage.setItem(storageKey, JSON.stringify(initialCart));
        }
        const stored = JSON.parse(localStorage.getItem(storageKey) || '[]');
        return Array.isArray(stored)
          ? stored.filter(
              (item: any): item is CartItem =>
                Boolean(item) &&
                typeof item.id === 'string' &&
                typeof item.name === 'string' &&
                Number.isFinite(item.price) &&
                Number.isInteger(item.quantity) &&
                item.quantity > 0
            )
          : [];
      } catch (error) {
        console.error('No se pudo recuperar el carrito.', error);
        return [];
      }
    };

    let items: CartItem[] = readCart();

    const saveCart = () => {
      try {
        localStorage.setItem(storageKey, JSON.stringify(items));
      } catch (error) {
        console.error('No se pudo guardar el carrito.', error);
      }
    };

    const images: Record<string, string> = {
      eclat: 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=500&q=85',
      halo: 'https://images.unsplash.com/photo-1617038220319-276d3cfab638?auto=format&fit=crop&w=500&q=85',
      lumiere: 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=500&q=85',
      orbita: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=500&q=85',
      riviere: 'https://images.unsplash.com/photo-1611591437281-460bfbe1220a?auto=format&fit=crop&w=500&q=85'
    };

    const escapeHTML = (value: string) =>
      value.replace(/[&<>"']/g, character => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
      })[character] || character);

    const renderCart = () => {
      const quantity = items.reduce((total, item) => total + item.quantity, 0);
      const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

      document.querySelectorAll('.cart-count').forEach(element => {
        element.textContent = String(quantity);
      });
      const menuCartCount = document.querySelector('.menu-cart-count');
      if (menuCartCount) menuCartCount.textContent = String(quantity);

      if (selectedCount) {
        selectedCount.textContent = `${quantity} ${quantity === 1 ? 'pieza seleccionada' : 'piezas seleccionadas'}`;
      }

      const subtotalEl = document.querySelector('#subtotal');
      const totalEl = document.querySelector('#total');
      if (subtotalEl) subtotalEl.textContent = money.format(total);
      if (totalEl) totalEl.textContent = money.format(total);

      cartList.replaceChildren();

      const checkoutBtn = document.querySelector<HTMLButtonElement>('.checkout-button');

      if (items.length === 0) {
        const empty = document.createElement('p');
        empty.className = 'cart-empty';
        empty.textContent = 'Tu bolsa está esperando esa pieza especial.';
        cartList.append(empty);
        if (checkoutBtn) checkoutBtn.disabled = true;
        return;
      }

      if (checkoutBtn) checkoutBtn.disabled = false;

      items.forEach((item, index) => {
        const card = document.createElement('article');
        card.className = 'cart-card';
        card.dataset['cartId'] = item.id;
        card.dataset['cartIndex'] = String(index);
        const safeName = escapeHTML(item.name);
        const customization = [item.variant, item.engraving ? `Grabado: ${item.engraving}` : '']
          .filter(Boolean)
          .map(value => escapeHTML(value || ''))
          .join(' · ');
        card.innerHTML = `
          <a href="/producto?id=${encodeURIComponent(item.id)}&back=%2Fcarrito" aria-label="Ver ${safeName}">
            <img src="" alt="${safeName}">
          </a>
          <div class="cart-item-info">
            <p>Pieza ${String(index + 1).padStart(2, '0')}</p>
            <h2><a href="/producto?id=${encodeURIComponent(item.id)}&back=%2Fcarrito">${safeName}</a></h2>
            ${customization ? `<p class="cart-customization">${customization}</p>` : ''}
            <dl>
              <div><dt>Cantidad</dt><dd>
                <button type="button" class="quantity-button" data-action="decrease" aria-label="Disminuir cantidad">−</button>
                <span class="cart-item-quantity">${item.quantity}</span>
                <button type="button" class="quantity-button" data-action="increase" aria-label="Aumentar cantidad">+</button>
              </dd></div>
              <div><dt>Precio unitario</dt><dd>${money.format(item.price)}</dd></div>
              <div><dt>Precio</dt><dd>${money.format(item.price * item.quantity)}</dd></div>
            </dl>
          </div>
          <button class="remove-button" type="button" data-action="remove" aria-label="Eliminar ${item.name}">×</button>
        `;
        const image = card.querySelector('img');
        if (image) {
          image.src =
            images[item.id] ||
            'https://images.unsplash.com/photo-1617038220319-276d3cfab638?auto=format&fit=crop&w=500&q=85';
        }
        cartList.append(card);
      });
    };

    cartList.addEventListener('click', event => {
      const target = event.target as HTMLElement;
      const button = target.closest<HTMLButtonElement>('button[data-action]');
      if (!button) return;
      const card = button.closest<HTMLElement>('.cart-card');
      if (!card) return;
      const itemIndex = Number(card.dataset['cartIndex']);
      const item = items[itemIndex];
      if (!item) return;

      const action = button.dataset['action'];
      if (action === 'remove' || (action === 'decrease' && item.quantity === 1)) {
        items.splice(itemIndex, 1);
      } else if (action === 'decrease') {
        item.quantity -= 1;
      } else if (action === 'increase') {
        item.quantity += 1;
      }
      saveCart();
      renderCart();
    });

    const paymentModal = document.querySelector<HTMLElement>('.payment-modal');
    const paymentBackdrop = document.querySelector<HTMLElement>('.payment-backdrop');
    const closePayment = document.querySelector<HTMLButtonElement>('.payment-modal-close');
    const continuePayment = document.querySelector<HTMLButtonElement>('.payment-modal-continue');
    const modalTotal = document.querySelector<HTMLElement>('.payment-modal-total');

    const setPaymentModal = (open: boolean) => {
      if (!paymentModal || !paymentBackdrop) return;
      paymentModal.hidden = !open;
      paymentBackdrop.hidden = !open;
      document.body.classList.toggle('no-scroll', open);
      if (open) closePayment?.focus();
    };

    document.querySelector('.checkout-button')?.addEventListener('click', () => {
      if (items.length === 0) return;
      const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
      if (modalTotal) modalTotal.textContent = money.format(total);
      setPaymentModal(true);
    });
    closePayment?.addEventListener('click', () => setPaymentModal(false));
    paymentBackdrop?.addEventListener('click', () => setPaymentModal(false));
    continuePayment?.addEventListener('click', () => {
      setPaymentModal(false);
      void this.router.navigate(['/detalle-pedido']);
    });

    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') {
        setMenu(false);
        setPaymentModal(false);
      }
    });

    renderCart();
  }
}