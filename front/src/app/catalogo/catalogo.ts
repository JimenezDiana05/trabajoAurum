import { Component, AfterViewInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
}

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './catalogo.html',
  styleUrl: './catalogo.css',
  encapsulation: ViewEncapsulation.None
})
export class Catalogo implements AfterViewInit, OnDestroy {
  private toastTimer?: number;

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
    const menuBackdrop = document.querySelector<HTMLElement>('.menu-backdrop');
    const menuToggle = document.querySelector<HTMLElement>('.menu-toggle');
    const cart = document.querySelector<HTMLElement>('.cart-panel');
    const cartBackdrop = document.querySelector<HTMLElement>('.cart-backdrop');
    const cartItemsElement = document.querySelector<HTMLElement>('.cart-items');
    const cartEmpty = document.querySelector<HTMLElement>('.cart-empty');
    const cartSummary = document.querySelector<HTMLElement>('.cart-summary');
    const toast = document.querySelector<HTMLElement>('.toast');
    const orderDialog = document.querySelector<HTMLDialogElement>('.order-dialog');
    const products = [...document.querySelectorAll<HTMLElement>('.catalog-card')];
    const tabs = [...document.querySelectorAll<HTMLButtonElement>('.category-tab')];
    const count = document.querySelector<HTMLElement>('.result-count');
    const noResults = document.querySelector<HTMLElement>('.no-results');

    if (!menu || !cart || !menuToggle || !cartItemsElement || products.length === 0) return;

    const money = new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
      maximumFractionDigits: 0
    });
    const cartKey = 'aurum-cart';
    const favoritesKey = 'aurum-favorites';

    const readStoredArray = (key: string): any[] => {
      try {
        const value = JSON.parse(localStorage.getItem(key) || '[]');
        return Array.isArray(value) ? value : [];
      } catch (error) {
        console.error(`No se pudo leer ${key}.`, error);
        return [];
      }
    };

    let items: CartItem[] = readStoredArray(cartKey).filter(
      (item: any): item is CartItem =>
        Boolean(item) &&
        typeof item.id === 'string' &&
        typeof item.name === 'string' &&
        Number.isFinite(item.price) &&
        Number.isInteger(item.quantity) &&
        item.quantity > 0
    );

    let favorites: string[] = readStoredArray(favoritesKey).filter(
      (id: any): id is string => typeof id === 'string'
    );

    let activeCategory = new URLSearchParams(window.location.search).get('categoria') || 'todos';
    if (!tabs.some(tab => tab.dataset['category'] === activeCategory)) {
      activeCategory = 'todos';
    }

    const showToast = (message: string) => {
      if (!toast) return;
      toast.textContent = message;
      toast.classList.add('is-visible');
      window.clearTimeout(this.toastTimer);
      this.toastTimer = window.setTimeout(() => toast.classList.remove('is-visible'), 2600);
    };

    const save = (key: string, value: unknown) => {
      try {
        localStorage.setItem(key, JSON.stringify(value));
      } catch (error) {
        console.error(`No se pudo guardar ${key}.`, error);
        showToast('No se pudo guardar el cambio en este dispositivo.');
      }
    };

    const setScrollLock = (locked: boolean) => {
      document.body.classList.toggle('no-scroll', locked);
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
      document.querySelector<HTMLElement>('.menu-close')?.focus();
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
      if (cartSubtotal) cartSubtotal.textContent = money.format(subtotal);
      if (cartEmpty) cartEmpty.hidden = items.length > 0;
      if (cartSummary) cartSummary.hidden = items.length === 0;
      cartItemsElement.replaceChildren();

      document.querySelectorAll<HTMLButtonElement>('.catalog-card .add-to-cart').forEach(button => {
        const product = button.closest<HTMLElement>('.catalog-card');
        if (!product) return;
        const inCart = items.some(item => item.id === product.dataset['id']);
        button.textContent = inCart ? 'En la bolsa' : 'Agregar';
        button.setAttribute(
          'aria-label',
          `${inCart ? 'Añadir otra unidad de' : 'Agregar'} ${product.dataset['name']} a la bolsa`
        );
      });

      items.forEach(item => {
        const row = document.createElement('article');
        row.className = 'cart-item';
        const description = document.createElement('div');
        const name = document.createElement('h3');
        name.textContent = item.name;
        const price = document.createElement('p');
        price.textContent = money.format(item.price);
        const actions = document.createElement('div');
        actions.className = 'cart-item-actions';

        const controls: Array<[string, string, string]> = [
          ['decrease', `Quitar una unidad de ${item.name}`, '−'],
          ['increase', `Añadir una unidad de ${item.name}`, '+']
        ];

        for (const [action, label, text] of controls) {
          const button = document.createElement('button');
          button.className = 'quantity-button';
          button.type = 'button';
          button.dataset['action'] = action;
          button.dataset['id'] = item.id;
          button.setAttribute('aria-label', label);
          button.textContent = text;
          if (action === 'decrease') {
            actions.append(button);
          } else {
            const amount = document.createElement('span');
            amount.textContent = String(item.quantity);
            actions.append(amount, button);
          }
        }

        const remove = document.createElement('button');
        remove.className = 'remove-item';
        remove.type = 'button';
        remove.dataset['action'] = 'remove';
        remove.dataset['id'] = item.id;
        remove.textContent = 'Eliminar';
        actions.append(remove);
        description.append(name, price, actions);

        const total = document.createElement('strong');
        total.className = 'cart-item-total';
        total.textContent = money.format(item.price * item.quantity);
        row.append(description, total);
        cartItemsElement.append(row);
      });
    };

    const applyFilters = () => {
      const searchInput = document.querySelector<HTMLInputElement>('.search-input');
      const priceFilter = document.querySelector<HTMLSelectElement>('.price-filter');
      const brandFilter = document.querySelector<HTMLSelectElement>('.brand-filter');
      const materialFilter = document.querySelector<HTMLSelectElement>('.material-filter');

      const searchTerm = (searchInput?.value || '').trim().toLocaleLowerCase('es');
      const selectedPrice = priceFilter?.value || 'all';
      const selectedBrand = brandFilter?.value || 'all';
      const selectedMaterial = materialFilter?.value || 'all';
      let visibleCount = 0;

      products.forEach(product => {
        const price = Number(product.dataset['price'] || 0);
        const matchesCategory = activeCategory === 'todos' || product.dataset['category'] === activeCategory;
        const matchesSearch = `${product.dataset['name']} ${product.dataset['brand']} ${product.dataset['material']}`
          .toLocaleLowerCase('es')
          .includes(searchTerm);
        const matchesBrand = selectedBrand === 'all' || product.dataset['brand'] === selectedBrand;
        const matchesMaterial = selectedMaterial === 'all' || product.dataset['material'] === selectedMaterial;
        const matchesPrice =
          selectedPrice === 'all' ||
          (selectedPrice === 'under-10000' && price < 10000) ||
          (selectedPrice === '10000-25000' && price >= 10000 && price <= 25000) ||
          (selectedPrice === 'over-25000' && price > 25000);

        const isVisible = matchesCategory && matchesSearch && matchesBrand && matchesMaterial && matchesPrice;
        product.hidden = !isVisible;
        if (isVisible) visibleCount += 1;
      });

      if (count) count.textContent = `${visibleCount} ${visibleCount === 1 ? 'pieza' : 'piezas'}`;
      if (noResults) noResults.hidden = visibleCount > 0;
    };

    const setCategory = (category: string, updateAddress = true) => {
      activeCategory = category;
      tabs.forEach(tab => {
        const isActive = tab.dataset['category'] === category;
        tab.classList.toggle('is-active', isActive);
        tab.setAttribute('aria-pressed', String(isActive));
      });
      if (updateAddress) {
        const url = new URL(window.location.href);
        if (category === 'todos') url.searchParams.delete('categoria');
        else url.searchParams.set('categoria', category);
        window.history.replaceState({}, '', url);
      }
      applyFilters();
    };

    menuToggle.addEventListener('click', openMenu);
    document.querySelector('.menu-close')?.addEventListener('click', closeMenu);
    menuBackdrop?.addEventListener('click', closeMenu);
    document.querySelectorAll('.menu-links a').forEach(link => link.addEventListener('click', closeMenu));
    document.querySelector('.cart-close')?.addEventListener('click', closeCart);
    cartBackdrop?.addEventListener('click', closeCart);
    document.querySelector('.order-close')?.addEventListener('click', () => orderDialog?.close());
    orderDialog?.addEventListener('click', event => {
      if (event.target === orderDialog) orderDialog.close();
    });

    tabs.forEach(tab =>
      tab.addEventListener('click', () => {
        setCategory(tab.dataset['category'] || 'todos');
        document.querySelector('#productos')?.scrollIntoView({ behavior: 'smooth' });
      })
    );
    document.querySelector('.search-input')?.addEventListener('input', applyFilters);
    document.querySelector('.price-filter')?.addEventListener('change', applyFilters);
    document.querySelector('.brand-filter')?.addEventListener('change', applyFilters);
    document.querySelector('.material-filter')?.addEventListener('change', applyFilters);

    document.querySelectorAll<HTMLButtonElement>('.favorite-button').forEach(button => {
      const productId = button.closest<HTMLElement>('.catalog-card')?.dataset['id'] || '';
      const isFavorite = favorites.includes(productId);
      button.setAttribute('aria-pressed', String(isFavorite));
      button.textContent = isFavorite ? '♥' : '♡';
      button.addEventListener('click', () => {
        const favorite = button.getAttribute('aria-pressed') !== 'true';
        button.setAttribute('aria-pressed', String(favorite));
        button.textContent = favorite ? '♥' : '♡';
        favorites = favorite
          ? [...new Set([...favorites, productId])]
          : favorites.filter(id => id !== productId);
        save(favoritesKey, favorites);
        showToast(favorite ? 'Añadido a tus favoritos.' : 'Eliminado de tus favoritos.');
      });
    });

    document.querySelectorAll<HTMLButtonElement>('.catalog-card .add-to-cart').forEach(button => {
      button.addEventListener('click', () => {
        const product = button.closest<HTMLElement>('.catalog-card');
        if (!product) return;
        const id = product.dataset['id'] || '';
        const name = product.dataset['name'] || '';
        const price = Number(product.dataset['price'] || 0);
        const existing = items.find(item => item.id === id);
        if (existing) existing.quantity += 1;
        else items.push({ id, name, price, quantity: 1 });
        save(cartKey, items);
        renderCart();
        showToast(`${name} se añadió a tu bolsa.`);
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
      save(cartKey, items);
      renderCart();
    });

    document.querySelector('.checkout-button')?.addEventListener('click', () => {
      if (items.length === 0) return;
      const orderNumber = `AR-${new Date().getFullYear()}-${String(Date.now()).slice(-6)}`;
      items = [];
      save(cartKey, items);
      renderCart();
      closeCart();
      const orderInput = document.querySelector<HTMLInputElement>('#order-number');
      const orderMessage = document.querySelector('.order-message');
      if (orderInput) orderInput.value = orderNumber;
      if (orderMessage) {
        orderMessage.textContent = `¡Gracias! Tu pedido de demostración ${orderNumber} quedó registrado.`;
      }
      orderDialog?.showModal();
    });

    document.querySelector('.order-form')?.addEventListener('submit', event => {
      event.preventDefault();
      const input = document.querySelector<HTMLInputElement>('#order-number');
      const message = document.querySelector('.order-message');
      if (!input || !message) return;
      const number = input.value.trim();
      if (!number) {
        input.focus();
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

    document.querySelector('.back-to-top')?.addEventListener('click', () => {
      closeMenu();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    });

    renderCart();
    setCategory(activeCategory, false);
  }

  ngOnDestroy(): void {
    if (typeof window === 'undefined') return;

    window.clearTimeout(this.toastTimer);
  }
}