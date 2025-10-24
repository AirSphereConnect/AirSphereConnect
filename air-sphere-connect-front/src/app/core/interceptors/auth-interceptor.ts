import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { UserService } from '../../shared/services/user-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router, private userService: UserService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authReq = req.clone({ withCredentials: true });
    console.log('Requête envoyée avec headers:', req.headers);
    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        if (
          error.status === 401 &&
          !req.url.endsWith('/api/profile') &&
          !req.url.endsWith('/api/favorites/new')
        ) {
          this.userService.logout().subscribe(() => {
            this.router.navigate(['/auth/login']);
          });
        }
        return throwError(() => error);
      })
    );
  }
}
