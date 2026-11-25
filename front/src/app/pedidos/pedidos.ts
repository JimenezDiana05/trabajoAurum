import { Component, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css',
  encapsulation: ViewEncapsulation.None
})
export class Pedidos implements AfterViewInit {
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

    const cartKey = 'aurum-cart';
    let items: Array<{ quantity?: number }> = [];
    try {
      const stored = JSON.parse(localStorage.getItem(cartKey) || '[]');
      if (Array.isArray(stored)) items = stored;
    } catch (error) {
      console.error('No se pudo leer el carrito.', error);
    }

    const count = items.reduce((total, item) => total + (item.quantity || 0), 0);
    document.querySelectorAll('.cart-count').forEach(element => {
      element.textContent = String(count);
    });
    const menuCartCount = document.querySelector('.menu-cart-count');
    if (menuCartCount) menuCartCount.textContent = String(count);

    const newOrders = document.querySelector<HTMLElement>('#new-orders');
    if (newOrders) {
      try {
        const stored = JSON.parse(localStorage.getItem('aurum-orders') || '[]');
        if (Array.isArray(stored)) {
          const money = new Intl.NumberFormat('es-MX', {
            style: 'currency',
            currency: 'MXN',
            maximumFractionDigits: 0
          });
          const images: Record<string, string> = {
            eclat: 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&w=500&q=85',
            halo: 'https://images.unsplash.com/photo-1617038220319-276d3cfab638?auto=format&fit=crop&w=500&q=85',
            lumiere: 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=500&q=85',
            orbita: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=500&q=85',
            riviere: 'https://images.unsplash.com/photo-1611591437281-460bfbe1220a?auto=format&fit=crop&w=500&q=85'
          };
          stored.slice(0, 4).forEach((order: any) => {
            if (!order || !Array.isArray(order.items)) return;
            order.items.forEach((item: any) => {
              if (!item || typeof item.name !== 'string') return;
              const card = document.createElement('article');
              card.className = 'order-card';
              card.innerHTML = `
                <img src="${images[item.id] || images['eclat']}" alt="${item.name}">
                <div class="order-info"><p class="status">Confirmado</p><h2>${item.name}</h2><small>N.º de orden ${order.number || ''}</small></div>
                <div class="order-meta"><small>Tipo de envío</small><strong>Envío asegurado</strong></div>
                <div class="order-meta"><small>Entrega estimada</small><strong>Por confirmar</strong></div>
                <div class="order-meta"><small>Monto</small><strong>${money.format(Number(item.price) * Number(item.quantity || 1))}</strong></div>
                <a class="detail-link" href="/producto?id=${encodeURIComponent(item.id)}&back=%2Fpedidos">Ver detalle</a>
              `;
              newOrders.append(card);
            });
          });
        }
      } catch (error) {
        console.error('No se pudieron recuperar los pedidos.', error);
      }
    }

    if (document.querySelector('.order-tabs')) {
      const isDelivered = new URLSearchParams(window.location.search).get('estado') === 'entregados';
      const tabs = [...document.querySelectorAll<HTMLElement>('.order-tab')];
      const activeOrders = document.querySelector<HTMLElement>('.active-orders');
      const deliveredOrders = document.querySelector<HTMLElement>('.delivered-orders');

      const updateTabs = (showDelivered: boolean) => {
        tabs.forEach((tab, index) =>
          tab.classList.toggle('is-active', showDelivered ? index === 1 : index === 0)
        );
        if (activeOrders) activeOrders.hidden = showDelivered;
        if (deliveredOrders) deliveredOrders.hidden = !showDelivered;
      };

      updateTabs(isDelivered);

      tabs.forEach((tab, index) => {
        tab.addEventListener('click', event => {
          event.preventDefault();
          const showDelivered = index === 1;
          const url = new URL(window.location.href);
          if (showDelivered) url.searchParams.set('estado', 'entregados');
          else url.searchParams.delete('estado');
          window.history.replaceState({}, '', url);
          updateTabs(showDelivered);
        });
      });
    }

    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') setMenu(false);
    });
  }
}