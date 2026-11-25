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
  selector: 'app-detalle-pedido',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './detalle-pedido.html',
  styleUrl: './detalle-pedido.css',
  encapsulation: ViewEncapsulation.None
})
export class DetallePedido implements AfterViewInit {
  private readonly router = inject(Router);

  ngAfterViewInit(): void {
    if (typeof document === 'undefined') return;

    const form = document.querySelector<HTMLFormElement>('.checkout-form');
    const submit = document.querySelector<HTMLButtonElement>('.place-order');
    const status = document.querySelector<HTMLElement>('.checkout-status');
    const cardNumber = document.querySelector<HTMLInputElement>('[name="cardNumber"]');
    const expiry = document.querySelector<HTMLInputElement>('[name="expiry"]');
    const cvv = document.querySelector<HTMLInputElement>('[name="cvv"]');
    const phone = document.querySelector<HTMLInputElement>('[name="phone"]');
    const postalCode = document.querySelector<HTMLInputElement>('[name="postalCode"]');
    const checkoutItems = document.querySelector<HTMLElement>('#checkout-items');

    const money = new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
      maximumFractionDigits: 0
    });

    let items: CartItem[] = [];
    try {
      const stored = JSON.parse(localStorage.getItem('aurum-cart') || '[]');
      if (Array.isArray(stored)) items = stored;
    } catch (error) {
      console.error('No se pudo recuperar el carrito.', error);
    }

    const total = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
    if (checkoutItems) {
      items.forEach(item => {
        const link = document.createElement('a');
        link.className = 'checkout-item';
        link.href = `/producto?id=${encodeURIComponent(item.id)}&back=%2Fdetalle-pedido`;
        link.setAttribute('aria-label', `Ver ${item.name}`);

        const image = document.createElement('img');
        image.src = `https://images.unsplash.com/${({
          eclat: 'photo-1605100804763-247f67b3557e',
          halo: 'photo-1617038220319-276d3cfab638',
          lumiere: 'photo-1535632066927-ab7c9ab60908',
          riviere: 'photo-1611591437281-460bfbe1220a',
          estela: 'photo-1611652022419-a9419f74343d',
          orbita: 'photo-1599643478518-a784e5dc4c8f',
          serein: 'photo-1611652022419-a9419f74343d',
          celeste: 'photo-1617038220319-276d3cfab638'
        } as Record<string, string>)[item.id] || 'photo-1605100804763-247f67b3557e'}?auto=format&fit=crop&w=160&q=85`;
        image.alt = '';

        const details = document.createElement('span');
        details.className = 'checkout-item-copy';
        const name = document.createElement('strong');
        name.textContent = item.name;
        const quantity = document.createElement('small');
        quantity.textContent = [
          `Cantidad: ${item.quantity}`,
          item.variant || '',
          item.engraving ? `Grabado: ${item.engraving}` : ''
        ].filter(Boolean).join(' · ');
        details.append(name, quantity);

        const price = document.createElement('strong');
        price.className = 'checkout-item-price';
        price.textContent = money.format(item.price * item.quantity);
        link.append(image, details, price);
        checkoutItems.append(link);
      });
    }
    document.querySelectorAll('.checkout-total').forEach(element => {
      element.textContent = money.format(total);
    });
    document.querySelectorAll('.checkout-count').forEach(element => {
      const quantity = items.reduce((sum, item) => sum + item.quantity, 0);
      element.textContent = `${quantity} ${quantity === 1 ? 'pieza' : 'piezas'}`;
    });

    cardNumber?.addEventListener('input', () => {
      cardNumber.value = cardNumber.value.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim();
      cardNumber.setCustomValidity('');
    });
    expiry?.addEventListener('input', () => {
      const digits = expiry.value.replace(/\D/g, '').slice(0, 4);
      expiry.value = digits.length > 2 ? `${digits.slice(0, 2)} / ${digits.slice(2)}` : digits;
      expiry.setCustomValidity('');
    });
    cvv?.addEventListener('input', () => {
      cvv.value = cvv.value.replace(/\D/g, '').slice(0, 4);
      cvv.setCustomValidity('');
    });
    phone?.addEventListener('input', () => {
      phone.value = phone.value.replace(/\D/g, '').slice(0, 10);
    });
    postalCode?.addEventListener('input', () => {
      postalCode.value = postalCode.value.replace(/\D/g, '').slice(0, 5);
    });

    form?.addEventListener('submit', event => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      if (!cardNumber || cardNumber.value.replace(/\s/g, '').length !== 16) {
        cardNumber?.setCustomValidity('Ingresa los 16 dígitos de la tarjeta.');
        cardNumber?.reportValidity();
        return;
      }
      if (!expiry || !/^\d{2} \/ \d{2}$/.test(expiry.value)) {
        expiry?.setCustomValidity('Ingresa una fecha válida en formato MM / AA.');
        expiry?.reportValidity();
        return;
      }
      const [monthText, yearText] = expiry.value.replace(' ', '').split('/');
      const month = Number(monthText);
      const year = 2000 + Number(yearText);
      const now = new Date();
      if (month < 1 || month > 12 || year < now.getFullYear() ||
          (year === now.getFullYear() && month < now.getMonth() + 1)) {
        expiry.setCustomValidity('La tarjeta está vencida o la fecha no es válida.');
        expiry.reportValidity();
        return;
      }
      if (!cvv || cvv.value.length < 3) {
        cvv?.setCustomValidity('Ingresa el código de seguridad.');
        cvv?.reportValidity();
        return;
      }
      if (items.length === 0) {
        if (status) status.textContent = 'El carrito está vacío.';
        return;
      }

      const orderNumber = `AUR-${Math.floor(100000 + Math.random() * 900000)}`;
      const data = new FormData(form);
      const order = {
        number: orderNumber,
        date: new Date().toISOString(),
        total,
        items,
        customer: {
          name: `${String(data.get('firstName') || '')} ${String(data.get('lastName') || '')}`.trim(),
          email: String(data.get('email') || ''),
          phone: String(data.get('phone') || ''),
          address: String(data.get('address') || ''),
          city: String(data.get('city') || ''),
          state: String(data.get('state') || ''),
          postalCode: String(data.get('postalCode') || '')
        }
      };

      try {
        const previous = JSON.parse(localStorage.getItem('aurum-orders') || '[]');
        const orders = Array.isArray(previous) ? [order, ...previous] : [order];
        localStorage.setItem('aurum-orders', JSON.stringify(orders));
        localStorage.setItem('aurum-cart', '[]');
      } catch (error) {
        console.error('No se pudo guardar el pedido.', error);
        if (status) status.textContent = 'No se pudo guardar el pedido. Intenta nuevamente.';
        return;
      }

      if (submit) submit.disabled = true;
      void this.router.navigate(['/pedidos']);
    });
  }
}
