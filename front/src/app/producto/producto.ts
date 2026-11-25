import { Component, OnInit, AfterViewInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

interface ProductData {
  name: string;
  category: string;
  collection: string;
  description: string;
  longDescription: string;
  price: string;
  amount: number;
  image: string;
  images: string[];
  delivery: string;
  optionLabel: string;
  options: string[];
  hasEngraving: boolean;
}

interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  variant?: string;
  engraving?: string;
}

const imageUrl = (id: string) =>
  `https://images.unsplash.com/${id}?auto=format&fit=crop&w=1200&q=85`;

const productImageIds: Record<string, string> = {
  eclat: 'photo-1605100804763-247f67b3557e',
  halo: 'photo-1617038220319-276d3cfab638',
  lumiere: 'photo-1535632066927-ab7c9ab60908',
  estela: 'photo-1611652022419-a9419f74343d',
  orbita: 'photo-1599643478518-a784e5dc4c8f',
  serein: 'photo-1611652022419-a9419f74343d',
  riviere: 'photo-1611591437281-460bfbe1220a',
  celeste: 'photo-1617038220319-276d3cfab638'
};

const productImage = (id: string) => imageUrl(productImageIds[id]);

const makeProduct = (
  id: string,
  name: string,
  category: string,
  collection: string,
  description: string,
  price: string,
  amount: number,
  optionLabel: string,
  options: string[],
  hasEngraving = false
): ProductData => {
  const image = productImage(id);
  return {
    name, category, collection, description, price, amount, optionLabel, options, hasEngraving, image,
    images: [image, image, image, image],
    delivery: '7 — 10 días hábiles',
    longDescription: `Una pieza ${description.toLocaleLowerCase('es')} elaborada con atención a cada detalle. Su diseño atemporal y acabado refinado la convierten en una joya para conservar toda la vida.`
  };
};

@Component({
  standalone: true,
  imports: [RouterLink],
  selector: 'app-producto',
  styleUrl: './producto.css',
  templateUrl: './producto.html'
})
export class Producto implements OnInit, AfterViewInit {
  private readonly products: Record<string, ProductData> = {
    eclat: makeProduct('eclat', 'Anillo Solitario Éclat', 'Anillos', 'Aurum', 'Oro 18K · Diamante 1.0 ct', '$32,100', 32100, 'Talla', ['5', '6', '7', '8', '9'], true),
    halo: makeProduct('halo', 'Anillo Halo', 'Anillos', 'Aurum Atelier', 'Oro 18K · Acabado pulido', '$12,600', 12600, 'Talla', ['5', '6', '7', '8', '9'], true),
    lumiere: makeProduct('lumiere', 'Pendientes Lumière', 'Pendientes', 'Aurum', 'Oro 18K · Diamantes', '$8,990', 8990, 'Cierre', ['Mariposa', 'Presión']),
    estela: makeProduct('estela', 'Aros Estela', 'Pendientes', 'Aurum Atelier', 'Oro 18K · Cierre seguro', '$11,400', 11400, 'Diámetro', ['Pequeño · 18 mm', 'Mediano · 24 mm']),
    orbita: makeProduct('orbita', 'Collar Órbita', 'Collares', 'Aurum', 'Oro 18K · Cadena 45 cm', '$24,600', 24600, 'Largo de cadena', ['40 cm', '45 cm', '50 cm']),
    serein: makeProduct('serein', 'Collar Serein', 'Collares', 'Aurum Atelier', 'Oro rosado · Dije brillante', '$18,900', 18900, 'Largo de cadena', ['40 cm', '45 cm', '50 cm'], true),
    riviere: makeProduct('riviere', 'Pulsera Rivière', 'Pulseras', 'Aurum', 'Oro 18K · Diamantes', '$18,490', 18490, 'Talla', ['16 cm', '18 cm', '20 cm']),
    celeste: makeProduct('celeste', 'Brazalete Celeste', 'Pulseras', 'Aurum Atelier', 'Plata · Edición especial', '$78,490', 78490, 'Talla', ['Pequeña', 'Mediana', 'Grande'])
  };

  product: ProductData = this.products['eclat'];
  selectedImage = this.product.image;
  selectedOption = this.product.options[0];
  quantity = 1;
  engraving = '';
  returnUrl = '/catalogo';
  addMessage = '';
  addedToCart = false;

  constructor(private readonly route: ActivatedRoute) {}

  get totalPrice(): string {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
      maximumFractionDigits: 0
    }).format(this.product.amount * this.quantity);
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const id = params.get('id') || '';
      this.product = this.products[id] || this.products['eclat'];
      this.selectedImage = this.product.image;
      this.selectedOption = this.product.options[0];
      this.quantity = 1;
      this.engraving = '';
      this.addedToCart = false;
      this.addMessage = '';

      const requestedReturn = params.get('back');
      const navigationReturn = typeof history !== 'undefined' && typeof history.state?.returnUrl === 'string'
        ? history.state.returnUrl
        : '';
      this.returnUrl = this.safeReturnUrl(navigationReturn || requestedReturn || '/catalogo');
    });
  }

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
    const menuToggle = document.querySelector<HTMLButtonElement>('.menu-toggle');
    const menuClose = document.querySelector<HTMLButtonElement>('.menu-close');
    const setMenu = (open: boolean) => {
      if (!menu || !backdrop || !menuToggle) return;
      menu.classList.toggle('is-open', open);
      menu.setAttribute('aria-hidden', String(!open));
      menuToggle.setAttribute('aria-expanded', String(open));
      backdrop.hidden = !open;
      document.body.classList.toggle('no-scroll', open);
    };
    menuToggle?.addEventListener('click', () => setMenu(true));
    menuClose?.addEventListener('click', () => setMenu(false));
    backdrop?.addEventListener('click', () => setMenu(false));
    menu?.querySelectorAll('.menu-links a').forEach(link =>
      link.addEventListener('click', () => setMenu(false))
    );
    document.addEventListener('keydown', event => {
      if (event.key === 'Escape') setMenu(false);
    });

    try {
      const stored = JSON.parse(localStorage.getItem('aurum-cart') || '[]');
      const count = Array.isArray(stored)
        ? stored.reduce((total: number, item: { quantity?: number }) => total + (item.quantity || 0), 0)
        : 0;
      document.querySelectorAll('.cart-count').forEach(element => element.textContent = String(count));
    } catch (error) {
      console.error('No se pudo leer el carrito.', error);
    }
  }

  selectImage(image: string): void {
    this.selectedImage = image;
  }

  selectOption(event: Event): void {
    const target = event.target;
    if (target instanceof HTMLSelectElement) this.selectedOption = target.value;
  }

  updateEngraving(event: Event): void {
    const target = event.target;
    if (target instanceof HTMLInputElement) this.engraving = target.value.slice(0, 18);
  }

  changeQuantity(change: number): void {
    this.quantity = Math.max(1, Math.min(20, this.quantity + change));
    this.addedToCart = false;
    this.addMessage = '';
  }

  addToCart(): void {
    let items: CartItem[] = [];
    try {
      const stored: unknown = JSON.parse(localStorage.getItem('aurum-cart') || '[]');
      if (Array.isArray(stored)) {
        items = stored.filter((item): item is CartItem =>
          Boolean(item) &&
          typeof item.id === 'string' &&
          typeof item.name === 'string' &&
          Number.isFinite(item.price) &&
          Number.isInteger(item.quantity) &&
          item.quantity > 0
        );
      }

      const engraving = this.engraving.trim();
      const currentItem = items.find(item =>
        item.id === this.route.snapshot.queryParamMap.get('id') &&
        item.variant === this.selectedOption &&
        item.engraving === engraving
      );
      if (currentItem) currentItem.quantity += this.quantity;
      else {
        items.push({
          id: this.route.snapshot.queryParamMap.get('id') || 'eclat',
          name: this.product.name,
          price: this.product.amount,
          quantity: this.quantity,
          variant: this.selectedOption,
          ...(engraving ? { engraving } : {})
        });
      }

      localStorage.setItem('aurum-cart', JSON.stringify(items));
      const count = items.reduce((total, item) => total + item.quantity, 0);
      document.querySelectorAll('.cart-count').forEach(element => {
        element.textContent = String(count);
      });
      this.addedToCart = true;
      this.addMessage = 'La pieza se agregó correctamente a tu bolsa.';
    } catch (error) {
      console.error('No se pudo agregar el producto a la bolsa.', error);
      this.addedToCart = false;
      this.addMessage = 'No se pudo agregar la pieza. Intenta nuevamente.';
    }
  }

  private safeReturnUrl(url: string): string {
    if (!url.startsWith('/') || url.startsWith('//') || url.startsWith('/producto')) return '/catalogo';
    return url;
  }
}
