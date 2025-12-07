import { TestBed } from '@angular/core/testing';
import { FormErrorService } from './form-error.service';
import {ValidationErrors} from '@angular/forms';

describe('FormErrorService', () => {
  let service: FormErrorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FormErrorService]
    });
    service = TestBed.inject(FormErrorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Generic error messages', () => {
    it('should return required message', () => {
      const errors = { required: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Ce champ est requis');
    });

    it('should return email message', () => {
      const errors = { email: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Veuillez entrer une adresse email valide');
    });

    it('should return minlength message with singular', () => {
      const errors = { minlength: { requiredLength: 4, actualLength: 3 } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('1 caractère manquant (minimum 4)');
    });

    it('should return minlength message with plural', () => {
      const errors = { minlength: { requiredLength: 8, actualLength: 5 } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('3 caractères manquants (minimum 8)');
    });

    it('should return maxlength message with singular', () => {
      const errors = { maxlength: { requiredLength: 10, actualLength: 11 } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('1 caractère en trop (maximum 10)');
    });

    it('should return maxlength message with plural', () => {
      const errors = { maxlength: { requiredLength: 10, actualLength: 15 } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('5 caractères en trop (maximum 10)');
    });

    it('should return pattern message', () => {
      const errors = { pattern: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Format invalide');
    });

    it('should return min message', () => {
      const errors = { min: { min: 18 } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('La valeur doit être au minimum 18');
    });

    it('should return max message', () => {
      const errors = { max: { max: 100 } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('La valeur ne doit pas dépasser 100');
    });
  });

  describe('Custom error messages', () => {
    it('should return invalidUsername message', () => {
      const errors = { invalidUsername: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Seuls les lettres, chiffres, _ et - sont autorisés');
    });

    it('should return invalidEmail message', () => {
      const errors = { invalidEmail: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Email invalide (ex: exemple@domaine.com)');
    });

    it('should return noUpperCase message', () => {
      const errors = { noUpperCase: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Doit contenir au moins 1 majuscule');
    });

    it('should return noLowerCase message', () => {
      const errors = { noLowerCase: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Doit contenir au moins 1 minuscule');
    });

    it('should return noNumber message', () => {
      const errors = { noNumber: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Doit contenir au moins 1 chiffre');
    });

    it('should return noSpecialChar message', () => {
      const errors = { noSpecialChar: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Doit contenir au moins 1 caractère spécial (@$!%*?&#)');
    });
  });

  describe('Field-specific error messages', () => {
    describe('username field', () => {
      it('should return username required message', () => {
        const errors = { required: true };
        const message = service.getErrorMessage(errors, 'username');
        expect(message).toBe("Nom d'utilisateur requis");
      });

      it('should return username minlength message', () => {
        const errors = { minlength: { requiredLength: 3, actualLength: 2 } };
        const message = service.getErrorMessage(errors, 'username');
        expect(message).toBe('Minimum 3 caractères');
      });

      it('should return username maxlength message', () => {
        const errors = { maxlength: { requiredLength: 20, actualLength: 25 } };
        const message = service.getErrorMessage(errors, 'username');
        expect(message).toBe('Maximum 20 caractères');
      });

      it('should return username invalidUsername message', () => {
        const errors = { invalidUsername: true };
        const message = service.getErrorMessage(errors, 'username');
        expect(message).toBe('Seuls les lettres, chiffres, _ et - sont autorisés');
      });
    });

    describe('email field', () => {
      it('should return email required message', () => {
        const errors = { required: true };
        const message = service.getErrorMessage(errors, 'email');
        expect(message).toBe('Email requis');
      });

      it('should return email format message', () => {
        const errors = { email: true };
        const message = service.getErrorMessage(errors, 'email');
        expect(message).toBe("Format d'email invalide");
      });

      it('should return email invalidEmail message', () => {
        const errors = { invalidEmail: true };
        const message = service.getErrorMessage(errors, 'email');
        expect(message).toBe('Email invalide (ex: exemple@domaine.com)');
      });
    });

    describe('password field', () => {
      it('should return password required message', () => {
        const errors = { required: true };
        const message = service.getErrorMessage(errors, 'password');
        expect(message).toBe('Mot de passe requis');
      });

      it('should return password minlength message', () => {
        const errors = { minlength: { requiredLength: 8, actualLength: 5 } };
        const message = service.getErrorMessage(errors, 'password');
        expect(message).toBe('Minimum 8 caractères');
      });

      it('should return password maxlength message', () => {
        const errors = { maxlength: { requiredLength: 50, actualLength: 55 } };
        const message = service.getErrorMessage(errors, 'password');
        expect(message).toBe('Maximum 50 caractères');
      });

      it('should return password noUpperCase message', () => {
        const errors = { noUpperCase: true };
        const message = service.getErrorMessage(errors, 'password');
        expect(message).toBe('Doit contenir au moins 1 majuscule');
      });

      it('should return password noLowerCase message', () => {
        const errors = { noLowerCase: true };
        const message = service.getErrorMessage(errors, 'password');
        expect(message).toBe('Doit contenir au moins 1 minuscule');
      });

      it('should return password noNumber message', () => {
        const errors = { noNumber: true };
        const message = service.getErrorMessage(errors, 'password');
        expect(message).toBe('Doit contenir au moins 1 chiffre');
      });

      it('should return password noSpecialChar message', () => {
        const errors = { noSpecialChar: true };
        const message = service.getErrorMessage(errors, 'password');
        expect(message).toBe('Doit contenir au moins 1 caractère spécial (@$!%*?&#)');
      });
    });

    describe('address field', () => {
      it('should return address required message', () => {
        const errors = { required: true };
        const message = service.getErrorMessage(errors, 'address');
        expect(message).toBe('Adresse requise');
      });

      it('should return address minlength message', () => {
        const errors = { minlength: { requiredLength: 5, actualLength: 3 } };
        const message = service.getErrorMessage(errors, 'address');
        expect(message).toBe('Minimum 5 caractères');
      });
    });

    describe('cityName field', () => {
      it('should return cityName required message', () => {
        const errors = { required: true };
        const message = service.getErrorMessage(errors, 'cityName');
        expect(message).toBe('Ville requise');
      });
    });
  });

  describe('Message priority', () => {
    it('should prioritize field-specific over custom messages', () => {
      const errors = { required: true };
      const message = service.getErrorMessage(errors, 'username');
      expect(message).toBe("Nom d'utilisateur requis");
    });

    it('should prioritize custom over generic messages', () => {
      const errors = { invalidEmail: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Email invalide (ex: exemple@domaine.com)');
    });

    it('should use generic message when no custom or field-specific exists', () => {
      const errors = { required: true };
      const message = service.getErrorMessage(errors, 'unknownField');
      expect(message).toBe('Ce champ est requis');
    });
  });

  describe('Edge cases', () => {
    it('should return null when errors is null', () => {
      const message = service.getErrorMessage(null);
      expect(message).toBeNull();
    });

    it('should return null when errors is undefined', () => {
      const message = service.getErrorMessage(null);
      expect(message).toBeNull();
    });

    it('should return default message for unknown error key', () => {
      const errors = { unknownError: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Ce champ contient une erreur');
    });

    it('should handle multiple errors and return first one', () => {
      const errors = { required: true, minlength: { requiredLength: 3 } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Ce champ est requis');
    });
  });

  describe('addCustomErrorMessage', () => {
    it('should add a new custom error message', () => {
      service.addCustomErrorMessage('customError', () => 'Custom error message');
      const errors = { customError: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Custom error message');
    });

    it('should override existing custom error message', () => {
      service.addCustomErrorMessage('invalidEmail', () => 'New email error message');
      const errors = { invalidEmail: true };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('New email error message');
    });

    it('should use error value in custom message', () => {
      service.addCustomErrorMessage('customError', (error) => `Custom error: ${error.value}`);
      const errors = { customError: { value: 'test' } };
      const message = service.getErrorMessage(errors);
      expect(message).toBe('Custom error: test');
    });
  });

  describe('addFieldSpecificErrorMessage', () => {
    it('should add a new field-specific error message', () => {
      service.addFieldSpecificErrorMessage('customField', 'required', () => 'Custom field required');
      const errors = { required: true };
      const message = service.getErrorMessage(errors, 'customField');
      expect(message).toBe('Custom field required');
    });

    it('should create field entry if it does not exist', () => {
      service.addFieldSpecificErrorMessage('newField', 'minlength', () => 'New field min length');
      const errors = { minlength: { requiredLength: 5 } };
      const message = service.getErrorMessage(errors, 'newField');
      expect(message).toBe('New field min length');
    });

    it('should override existing field-specific error message', () => {
      service.addFieldSpecificErrorMessage('username', 'required', () => 'Override username required');
      const errors = { required: true };
      const message = service.getErrorMessage(errors, 'username');
      expect(message).toBe('Override username required');
    });

    it('should add multiple error messages for same field', () => {
      service.addFieldSpecificErrorMessage('customField', 'required', () => 'Field required');
      service.addFieldSpecificErrorMessage('customField', 'minlength', () => 'Field too short');

      let errors: ValidationErrors;
      errors = { required: true };
      let message = service.getErrorMessage(errors, 'customField');
      expect(message).toBe('Field required');

      errors = { minlength: { requiredLength: 5 } };
      message = service.getErrorMessage(errors, 'customField');
      expect(message).toBe('Field too short');
    });

    it('should use error value in field-specific message', () => {
      service.addFieldSpecificErrorMessage('age', 'min', (error) => `Age must be at least ${error.min}`);
      const errors = { min: { min: 18 } };
      const message = service.getErrorMessage(errors, 'age');
      expect(message).toBe('Age must be at least 18');
    });
  });
});
