import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { GameApiService } from '../../services/game-api.service';
import { WorldList, CreateGameRequest } from '../../models/game.model';

@Component({
  selector: 'app-new-game',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './new-game.component.html',
  styleUrl: './new-game.component.css'
})
export class NewGameComponent implements OnInit, OnDestroy {
  private gameApiService = inject(GameApiService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private subscriptions: Subscription[] = [];

  worlds: WorldList[] = [];
  selectedWorldId: number | null = null;
  playerName = '';
  initialLife = 5;
  seed: number | null = null;
  useSeed = false;

  loading = false;
  loadingWorlds = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadWorlds();

    const sub = this.route.queryParams.subscribe(params => {
      if (params['worldId']) {
        this.selectedWorldId = +params['worldId'];
      }
    });
    this.subscriptions.push(sub);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadWorlds(): void {
    this.loadingWorlds = true;
    const sub = this.gameApiService.getAllWorlds().subscribe({
      next: (data) => {
        this.worlds = data;
        this.loadingWorlds = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar los mundos';
        this.loadingWorlds = false;
      }
    });
    this.subscriptions.push(sub);
  }

  onWorldChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedWorldId = select.value ? +select.value : null;
  }

  onSeedToggle(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    this.useSeed = checkbox.checked;
    if (!this.useSeed) {
      this.seed = null;
    }
  }

  onSubmit(form: any): void {
    if (form.invalid || !this.selectedWorldId) {
      return;
    }

    this.loading = true;
    this.error = null;

    const request: CreateGameRequest = {
      worldId: this.selectedWorldId,
      playerName: this.playerName.trim(),
      initialLife: this.initialLife,
      seed: this.useSeed && this.seed ? this.seed : undefined
    };

    const sub = this.gameApiService.createGame(request).subscribe({
      next: (game) => {
        this.loading = false;
        this.router.navigate(['/game', game.gameId]);
      },
      error: (err) => {
        this.loading = false;
        if (err.error?.details) {
          this.error = err.error.details.map((d: any) => `${d.field}: ${d.issue}`).join(', ');
        } else {
          this.error = err.error?.message || 'Error al crear la partida';
        }
      }
    });
    this.subscriptions.push(sub);
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}
