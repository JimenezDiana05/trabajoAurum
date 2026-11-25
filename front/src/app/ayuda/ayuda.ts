import { Component, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-ayuda',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './ayuda.html',
  styleUrl: './ayuda.css',
  encapsulation: ViewEncapsulation.None
})
export class Ayuda implements AfterViewInit {
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
    let cartItems: Array<{ quantity?: number }> = [];
    try {
      const stored = JSON.parse(localStorage.getItem(cartKey) || '[]');
      if (Array.isArray(stored)) cartItems = stored;
    } catch (error) {
      console.error('No se pudo leer el carrito.', error);
    }

    const count = cartItems.reduce((total, item) => total + (item.quantity || 0), 0);
    document.querySelectorAll('.cart-count').forEach(element => {
      element.textContent = String(count);
    });
    const menuCartCount = document.querySelector('.menu-cart-count');
    if (menuCartCount) menuCartCount.textContent = String(count);

    const search = document.querySelector<HTMLInputElement>('.faq-search input');
    const items = [...document.querySelectorAll<HTMLElement>('.faq-item')];
    const emptyMessage = document.querySelector<HTMLElement>('.faq-empty');

    if (search && emptyMessage) {
      const filterQuestions = () => {
        const query = search.value.trim().toLocaleLowerCase('es');
        let visibleCount = 0;

        items.forEach(item => {
          const matches = (item.textContent || '').toLocaleLowerCase('es').includes(query);
          item.hidden = !matches;
          if (!matches) {
            const button = item.querySelector<HTMLButtonElement>('.faq-question');
            const answer = item.querySelector<HTMLElement>('.faq-answer');
            const toggleIcon = button?.querySelector<HTMLElement>('.faq-toggle');
            button?.setAttribute('aria-expanded', 'false');
            if (toggleIcon) toggleIcon.textContent = '+';
            if (answer) answer.hidden = true;
          } else {
            visibleCount += 1;
          }
        });

        emptyMessage.hidden = visibleCount > 0;
      };

      search.addEventListener('input', filterQuestions);
    }

    document.querySelectorAll<HTMLButtonElement>('.faq-question').forEach(button => {
      button.addEventListener('click', () => {
        const expanded = button.getAttribute('aria-expanded') === 'true';
        const answerId = button.getAttribute('aria-controls') || '';
        const answer = document.getElementById(answerId);
        const toggleIcon = button.querySelector<HTMLElement>('.faq-toggle');

        button.setAttribute('aria-expanded', String(!expanded));
        if (toggleIcon) toggleIcon.textContent = expanded ? '+' : '−';
        if (answer) answer.hidden = expanded;
      });
    });

    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') setMenu(false);
    });
  }
}