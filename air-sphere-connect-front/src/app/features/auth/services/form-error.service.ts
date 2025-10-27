import { Injectable } from '@angular/core';
import { ValidationErrors } from '@angular/forms';

export interface ErrorMessageConfig {
  [key: string]: (error?: any) => string;
}

@Injectable({
  providedIn: 'root'
})
export class FormErrorService {

  /**
   * Messages d'erreur génériques
   */
  private genericErrorMessages: ErrorMessageConfig = {
    required: () => 'Ce champ est requis',
    email: () => 'Veuillez entrer une adresse email valide',
    minlength: (error) => {
      const remaining = error.requiredLength - error.actualLength;
      return `${remaining} caractère${remaining > 1 ? 's' : ''} manquant${remaining > 1 ? 's' : ''} (minimum ${error.requiredLength})`;
    },
    maxlength: (error) => {
      const excess = error.actualLength - error.requiredLength;
      return `${excess} caractère${excess > 1 ? 's' : ''} en trop (maximum ${error.requiredLength})`;
    },
    pattern: () => 'Format invalide',
    min: (error) => `La valeur doit être au minimum ${error.min}`,
    max: (error) => `La valeur ne doit pas dépasser ${error.max}`,
  };


  private customErrorMessages: ErrorMessageConfig = {
    // nom d'utilisateur
    invalidUsername: () => 'Seuls les lettres, chiffres, _ et - sont autorisés',

    // email
    invalidEmail: () => 'Email invalide (ex: exemple@domaine.com)',

    // mot de passe
    noUpperCase: () => 'Doit contenir au moins 1 majuscule',
    noLowerCase: () => 'Doit contenir au moins 1 minuscule',
    noNumber: () => 'Doit contenir au moins 1 chiffre',
  };


  private fieldSpecificMessages: { [fieldName: string]: ErrorMessageConfig } = {
    username: {
      required: () => "Nom d'utilisateur requis",
      minlength: () => 'Minimum 3 caractères',
      maxlength: () => 'Maximum 20 caractères',
      invalidUsername: () => 'Seuls les lettres, chiffres, _ et - sont autorisés',
    },
    email: {
      required: () => 'Email requis',
      email: () => "Format d'email invalide",
      invalidEmail: () => 'Email invalide (ex: exemple@domaine.com)',
    },
    password: {
      required: () => 'Mot de passe requis',
      minlength: () => 'Minimum 8 caractères',
      maxlength: () => 'Maximum 50 caractères',
      noUpperCase: () => 'Doit contenir au moins 1 majuscule',
      noLowerCase: () => 'Doit contenir au moins 1 minuscule',
      noNumber: () => 'Doit contenir au moins 1 chiffre',
    },
    address: {
      required: () => 'Adresse requise',
      minlength: () => 'Minimum 5 caractères',
    },
    cityName: {
      required: () => 'Ville requise',
    },
  };


  getErrorMessage(errors: ValidationErrors | null, fieldName?: string): string | null {
    if (!errors) return null;

    const firstErrorKey = Object.keys(errors)[0];
    const errorValue = errors[firstErrorKey];

    if (fieldName && this.fieldSpecificMessages[fieldName]?.[firstErrorKey]) {
      return this.fieldSpecificMessages[fieldName][firstErrorKey](errorValue);
    }

    if (this.customErrorMessages[firstErrorKey]) {
      return this.customErrorMessages[firstErrorKey](errorValue);
    }

    if (this.genericErrorMessages[firstErrorKey]) {
      return this.genericErrorMessages[firstErrorKey](errorValue);
    }

    return 'Ce champ contient une erreur';
  }


  addCustomErrorMessage(errorKey: string, messageFn: (error?: any) => string): void {
    this.customErrorMessages[errorKey] = messageFn;
  }


  addFieldSpecificErrorMessage(
    fieldName: string,
    errorKey: string,
    messageFn: (error?: any) => string
  ): void {
    if (!this.fieldSpecificMessages[fieldName]) {
      this.fieldSpecificMessages[fieldName] = {};
    }
    this.fieldSpecificMessages[fieldName][errorKey] = messageFn;
  }
}
