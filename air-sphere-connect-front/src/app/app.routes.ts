import { Routes } from '@angular/router';
import {AuthGuard} from './core/guards/AuthGuard';

export const routes: Routes = [
  { path: 'home', loadComponent: () => import('./features/home/home').then(m => m.Home) },

  { path: 'auth/login', loadComponent: () => import('./features/auth/login/login').then(m => m.Login) },
  { path: 'auth/register', loadComponent: () => import('./features/auth/register/register').then(m => m.Register) },
  { path: 'auth/profile', loadComponent: () => import('./features/auth/profile/profile/profile').then(m => m.Profile)},
  { path: 'auth/profile/user/edit', loadComponent: () => import('./features/auth/profile/user-edit/user-edit').then(m => m.UserEdit)},
  { path: 'auth/settings', loadComponent: () => import('./features/auth/settings/settings').then(m => m.Settings) },

  /*{ path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard/dashboard').then(m => m.Dashboard), canActivate:[AuthGuard] },
  { path: 'dashboard/air-quality', loadComponent: () => import('./features/dashboard/widgets/air-quality-widget/air-quality-widget').then(m => m.AirQualityWidget), canActivate:[AuthGuard] },
  { path: 'dashboard/weather', loadComponent: () => import('./features/dashboard/widgets/weather-widget/weather-widget').then(m => m.WeatherWidget) },
  { path: 'dashboard/pollution-trends', loadComponent: () => import('./features/dashboard/widgets/pollution-trend-widget/pollution-trend-widget').then(m => m.PollutionTrendWidget), canActivate:[AuthGuard] },
*/
  { path: 'forum', loadComponent: () => import('./features/forum/components/forum/forum').then(m => m.Forum) },
  { path: 'forum/thread-detail/:id', loadComponent: () => import('./features/forum/components/thread-detail/thread-detail').then(m => m.ThreadDetail) },
  { path: 'forum/thread-list/:id', loadComponent: () => import('./features/forum/components/thread-list/thread-list').then(m => m.ThreadList) },
  { path: 'forum/post/:id', loadComponent: () => import('./features/forum/components/post/post').then(m => m.Post) },

  { path: 'admin/users', loadComponent: () => import('./features/admin/user-management/user-management').then(m => m.UserManagement) },
  { path: 'admin/moderation', loadComponent: () => import('./features/admin/content-moderation/content-moderation').then(m => m.ContentModeration) },

  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: '**', loadComponent: () => import('./features/not-found/not-found').then(m => m.NotFound) }
];
