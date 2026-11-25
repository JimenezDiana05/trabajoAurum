import { Component, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-recuperar-contrasena',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './recuperar-contrasena.html',
  styleUrl: './recuperar-contrasena.css',
  encapsulation: ViewEncapsulation.None
})
export class RecuperarContrasena implements AfterViewInit {
  ngAfterViewInit(): void {
    if (typeof document === 'undefined') return;
    const form = document.querySelector<HTMLFormElement>('.recovery-form');
    const message = document.querySelector<HTMLElement>('.auth-message');
    if (!form || !message) return;

    form.addEventListener('submit', event => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      message.textContent = 'El envío del código aún no está conectado a un servicio de correo.';
    });
  }
}