import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { UserService } from '../../shared/services/user-service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const userService = inject(UserService);

  const authReq = req.clone({ withCredentials: true });
  console.log('Requête envoyée avec headers:', req.headers);

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (
        error.status === 401 &&
        !req.url.endsWith('/api/profile')
      ) {
        userService.logout().subscribe(() => {
          router.navigate(['/auth/login']);
        });
      }
      return throwError(() => error);
    })
  );
};
