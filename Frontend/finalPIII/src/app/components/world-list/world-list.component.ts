import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { GameApiService } from '../../services/game-api.service';
import { WorldList } from '../../models/game.model';

@Component({
  selector: 'app-world-list',
  standalone: true,
  templateUrl: './world-list.component.html',
  styleUrl: './world-list.component.css'
})
export class WorldListComponent implements OnInit, OnDestroy {
  private gameApiService = inject(GameApiService);
  private router = inject(Router);
  private subscription: Subscription | null = null;

  worlds: WorldList[] = [];
  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadWorlds();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  loadWorlds(): void {
    this.loading = true;
    this.error = null;

    this.subscription = this.gameApiService.getAllWorlds().subscribe({
      next: (data) => {
        this.worlds = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar los mundos';
        this.loading = false;
      }
    });
  }

  selectWorld(worldId: number): void {
    this.router.navigate(['/new-game'], { queryParams: { worldId } });
  }

  goToRanking(): void {
    this.router.navigate(['/ranking']);
  }
}
