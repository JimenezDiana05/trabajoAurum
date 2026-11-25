import { Routes } from '@angular/router';
import { Inicio } from './inicio/inicio';
import { Catalogo } from './catalogo/catalogo';
import { Carrito } from './carrito/carrito';
import { Pedidos } from './pedidos/pedidos';
import { Perfil } from './perfil/perfil';
import { Ayuda } from './ayuda/ayuda';
import { CrearCuenta } from './crear-cuenta/crear-cuenta';
import { Login } from './login/login';
import { RecuperarContrasena } from './recuperar-contrasena/recuperar-contrasena';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'catalogo', component: Catalogo },
  { path: 'producto', loadComponent: () => import('./producto/producto').then(module => module.Producto) },
  { path: 'carrito', component: Carrito },
  { path: 'pedidos', component: Pedidos },
  { path: 'detalle-pedido', loadComponent: () => import('./detalle-pedido/detalle-pedido').then(module => module.DetallePedido) },
  { path: 'perfil', component: Perfil },
  { path: 'ayuda', component: Ayuda },
  { path: 'crear-cuenta', component: CrearCuenta },
  { path: 'crearcuenta', redirectTo: 'crear-cuenta', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'recuperar-contrasena', component: RecuperarContrasena },
  { path: 'recuperar--contrasena', redirectTo: 'recuperar-contrasena', pathMatch: 'full' },
  { path: '**', redirectTo: '' }
];