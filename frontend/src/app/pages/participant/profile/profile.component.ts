import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService, User } from '../../../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule]
})
export class ProfileComponent implements OnInit {
  // User information
  username: string | null = null;
  userAvatar: string | null = null;
  userInitials: string = 'P';
  userEmail: string | null = null;
  userRole: string | null = null;

  // Sidebar state
  sidebarActive: boolean = false;

  // Profile form
  profileForm: FormGroup;
  isEditMode: boolean = false;
  formSubmitted: boolean = false;
  updateSuccess: boolean = false;
  updateError: string | null = null;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.profileForm = this.formBuilder.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telephone: [''],
      adresse: ['']
    });
  }

  ngOnInit(): void {
    this.loadUserInfo();
    this.initializeForm();
  }

  loadUserInfo(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.username = currentUser.name || currentUser.email || 'Participant';
      this.userEmail = currentUser.email;
      this.userRole = currentUser.role;
      this.userInitials = this.getInitials(this.username);
    } else {
      this.username = 'Participant';
      this.userInitials = 'P';
      this.router.navigate(['/login']);
    }
  }

  initializeForm(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.profileForm.patchValue({
        name: currentUser.name || '',
        email: currentUser.email || '',
        telephone: '',  // These fields are not available in the current User model
        adresse: ''     // but we're preparing the UI for future implementation
      });
    }
  }

  getInitials(name: string | null): string {
    if (!name) return 'P';

    const parts = name.split(' ');
    if (parts.length === 1) {
      return parts[0].charAt(0).toUpperCase();
    }

    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
  }

  toggleSidebar(): void {
    this.sidebarActive = !this.sidebarActive;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  toggleEditMode(): void {
    this.isEditMode = !this.isEditMode;
    this.updateSuccess = false;
    this.updateError = null;

    if (!this.isEditMode) {
      // Reset form to original values when canceling edit
      this.initializeForm();
    }
  }

  onSubmit(): void {
    this.formSubmitted = true;

    if (this.profileForm.valid) {
      // In a real implementation, we would call a service to update the user profile
      // For now, we'll just simulate a successful update
      setTimeout(() => {
        this.updateSuccess = true;
        this.isEditMode = false;
        this.formSubmitted = false;

        // Update the displayed user information
        this.username = this.profileForm.value.name;
        this.userEmail = this.profileForm.value.email;
        this.userInitials = this.getInitials(this.username);
      }, 1000);
    }
  }
}
