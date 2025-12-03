import {Component, inject} from '@angular/core';
import { ErrorMessageService } from '../../../services/error-message-service';

@Component({
  selector: 'app-error-message',
  templateUrl: './error-message.html',
  styleUrl: './error-message.scss'
})
export class ErrorMessage {

  private readonly errorMessageService = inject(ErrorMessageService);

  message = this.errorMessageService.message;

}
