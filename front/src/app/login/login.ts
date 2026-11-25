import { Component, AfterViewInit, ViewEncapsulation, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
  encapsulation: ViewEncapsulation.None
})
export class Login implements AfterViewInit {
  private router = inject(Router);

  ngAfterViewInit(): void {
    if (typeof document === 'undefined') return;

    const form = document.querySelector<HTMLFormElement>('.login-form');
    const message = document.querySelector<HTMLElement>('.auth-message');
    if (!form || !message) return;

    form.addEventListener('submit', event => {
      event.preventDefault();
      if (!form.reportValidity()) return;
      this.router.navigate(['/']);
    });
  }
}