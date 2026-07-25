import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './navbar/navbar';
import { Sidebar } from './sidebar/sidebar';

@Component({
  selector: 'sge-layout',
  imports: [RouterOutlet, Navbar, Sidebar],
  template: `
    <sge-navbar></sge-navbar>
    <div class="layout">
      <sge-sidebar></sge-sidebar>
      <main class="content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styleUrl: '../app.scss'
})
export class Layout {}
