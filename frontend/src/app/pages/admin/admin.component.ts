import { Component } from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  imports: [
    NgClass,
    NgForOf,
    NgIf
  ],
  styleUrls: ['./admin.component.css']
})
export class AdminComponent {
  activeMenu: string = 'dashboard';
  subMenus: { [key: string]: boolean } = {
    users: false,
    quiz: false
  };

  latestUsers = [
    { photo: 'assets/avatar1.png', name: 'Sophie Martin', email: 'sophie@example.com', role: 'Professeur' },
    { photo: 'assets/avatar2.png', name: 'Thomas Dubois', email: 'thomas@example.com', role: 'Étudiant' },
    { photo: 'assets/avatar3.png', name: 'Julie Bernard', email: 'julie@example.com', role: 'Modérateur' },
    { photo: 'assets/avatar4.png', name: 'Marc Petit', email: 'marc@example.com', role: 'Étudiant' }
  ];

  recentQuizzes = [
    { title: 'Culture Générale Niveau 3', author: 'Sophie Martin', date: '05-09-2025', status: 'Publié', statusClass: 'published' },
    { title: 'Mathématiques Avancées', author: 'Thomas Dubois', date: '04-09-2025', status: 'En attente', statusClass: 'pending' },
    { title: 'Histoire de France', author: 'Julie Bernard', date: '03-09-2025', status: 'Publié', statusClass: 'published' },
    { title: 'Sciences Naturelles', author: 'Marc Petit', date: '02-09-2025', status: 'Publié', statusClass: 'published' }
  ];

  setActiveMenu(menu: string): void {
    this.activeMenu = menu;
    console.log('Menu actif:', menu);
  }

  toggleSubMenu(menu: string): void {
    this.subMenus[menu] = !this.subMenus[menu];
  }


}
