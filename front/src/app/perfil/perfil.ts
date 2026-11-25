import { Component, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
  encapsulation: ViewEncapsulation.None
})
export class Perfil implements AfterViewInit {
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

    const form = document.querySelector<HTMLFormElement>('.profile-fields');
    const message = document.querySelector<HTMLElement>('.profile-message');

    form?.addEventListener('submit', event => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      if (message) {
        message.textContent =
          'Los cambios están listos, pero el guardado aún no está conectado a una cuenta.';
      }
    });

    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') setMenu(false);
    });
  }
}