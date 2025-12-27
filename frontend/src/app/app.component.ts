import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TelaBreveComponent} from './components/tela-breve/tela-breve.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TelaBreveComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';
}
