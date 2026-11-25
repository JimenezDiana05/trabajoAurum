import { Component, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-crear-cuenta',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './crear-cuenta.html',
  styleUrl: './crear-cuenta.css',
  encapsulation: ViewEncapsulation.None
})
export class CrearCuenta implements AfterViewInit {
  ngAfterViewInit(): void {
    if (typeof document === 'undefined') return;
    const form = document.querySelector<HTMLFormElement>('.register-form');
    const password = document.querySelector<HTMLInputElement>('.register-password');
    const confirmation = document.querySelector<HTMLInputElement>('.register-confirm');
    const submit = document.querySelector<HTMLButtonElement>('.register-submit');
    const message = document.querySelector<HTMLElement>('.password-message');

    const rules: Record<string, (value: string) => boolean> = {
      length: value => value.length >= 8,
      uppercase: value => /[A-ZÁÉÍÓÚÑ]/.test(value),
      special: value => /[^A-Za-z0-9ÁÉÍÓÚÑáéíóúñ]/.test(value)
    };

    if (!form || !password || !confirmation || !submit || !message) return;

    const updatePasswordState = () => {
      const value = password.value;
      const passwordIsValid = Object.entries(rules).every(([name, check]) => {
        const isValid = check(value);
        document.querySelector(`[data-rule="${name}"]`)?.classList.toggle('is-valid', isValid);
        return isValid;
      });

      const passwordsMatch = value.length > 0 && value === confirmation.value;
      message.textContent =
        confirmation.value && !passwordsMatch
          ? 'Las contraseñas no coinciden.'
          : confirmation.value && passwordsMatch
            ? 'Las contraseñas coinciden.'
            : '';

      submit.disabled = !(passwordIsValid && passwordsMatch);
    };

    password.addEventListener('input', updatePasswordState);
    confirmation.addEventListener('input', updatePasswordState);

    form.addEventListener('submit', event => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      updatePasswordState();
      if (submit.disabled) {
        confirmation.focus();
        return;
      }
      message.textContent = 'El registro aún no está conectado a un servicio de cuentas.';
    });
  }
}