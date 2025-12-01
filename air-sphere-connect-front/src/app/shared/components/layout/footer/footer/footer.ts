import {Component, Input} from '@angular/core';
import {FooterAbout} from '../footer-about/footer-about';
import {FooterContact} from '../footer-contact/footer-contact';
import {FooterLinks} from '../footer-links/footer-links';
import {FooterSocial} from '../footer-social/footer-social';

@Component({
  selector: 'app-footer',
  imports: [
    FooterAbout,
    FooterContact,
    FooterLinks,
    FooterSocial
  ],
  templateUrl: './footer.html',
  styleUrls: ['./footer.scss']
})
export class Footer {
  @Input() userRole!: string | null;

}
