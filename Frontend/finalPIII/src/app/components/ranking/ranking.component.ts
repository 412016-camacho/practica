import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { GameApiService } from '../../services/game-api.service';
import { RankingItem } from '../../models/game.model';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.css'
})
export class RankingComponent implements OnInit, OnDestroy {
  private gameApiService = inject(GameApiService);
  private router = inject(Router);
  private subscription: Subscription | null = null;

  rankingItems: RankingItem[] = [];
  topFilter = 10;
  loading = false;
  error: string | null = null;

  ngOnInit(): void {
    this.loadRanking();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  loadRanking(): void {
    this.loading = true;
    this.error = null;

    if (this.subscription) {
      this.subscription.unsubscribe();
    }

    this.subscription = this.gameApiService.getRanking(this.topFilter).subscribe({
      next: (data) => {
        this.rankingItems = data.items;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar el ranking';
        this.loading = false;
      }
    });
  }

  onTopFilterChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.topFilter = +select.value;
    this.loadRanking();
  }

  onRefresh(): void {
    this.loadRanking();
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }

  getMedalEmoji(index: number): string {
    switch (index) {
      case 0:
        return '🥇';
      case 1:
        return '🥈';
      case 2:
        return '🥉';
      default:
        return '';
    }
  }
}
