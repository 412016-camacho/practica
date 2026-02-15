import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { GameApiService } from '../../services/game-api.service';
import { GameResponse, TurnResponse, TurnHistoryItem } from '../../models/game.model';
import { GameBoardComponent } from '../game-board/game-board.component';
import { TurnHistoryComponent } from '../turn-history/turn-history.component';

@Component({
  selector: 'app-game-view',
  standalone: true,
  imports: [GameBoardComponent, TurnHistoryComponent],
  templateUrl: './game-view.component.html',
  styleUrl: './game-view.component.css'
})
export class GameViewComponent implements OnInit, OnDestroy {
  private gameApiService = inject(GameApiService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private subscriptions: Subscription[] = [];

  game: GameResponse | null = null;
  lastTurn: TurnResponse | null = null;
  turnHistory: TurnHistoryItem[] = [];
  gameId: number | null = null;

  loading = false;
  playingTurn = false;
  error: string | null = null;

  ngOnInit(): void {
    const sub = this.route.params.subscribe(params => {
      this.gameId = +params['gameId'];
      this.loadGame();
      this.loadTurnHistory();
    });
    this.subscriptions.push(sub);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadGame(): void {
    if (!this.gameId) return;

    this.loading = true;
    this.error = null;

    const sub = this.gameApiService.getGameById(this.gameId).subscribe({
      next: (data) => {
        this.game = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar la partida';
        this.loading = false;
      }
    });
    this.subscriptions.push(sub);
  }

  loadTurnHistory(): void {
    if (!this.gameId) return;

    const sub = this.gameApiService.getTurnHistory(this.gameId).subscribe({
      next: (data) => {
        this.turnHistory = data.turns;
      },
      error: (err) => {
        console.error('Error al cargar historial:', err);
      }
    });
    this.subscriptions.push(sub);
  }

  onPlayTurn(diceOverride: number | null): void {
    if (!this.gameId) return;

    this.playingTurn = true;
    this.error = null;

    const request = diceOverride ? { diceOverride } : undefined;

    const sub = this.gameApiService.playTurn(this.gameId, request).subscribe({
      next: (turn) => {
        this.lastTurn = turn;
        this.playingTurn = false;
        this.loadGame();
        this.loadTurnHistory();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al jugar turno';
        this.playingTurn = false;
      }
    });
    this.subscriptions.push(sub);
  }

  onRefreshHistory(): void {
    this.loadTurnHistory();
  }

  onTurnSelected(turn: TurnHistoryItem): void {
    console.log('Turno seleccionado:', turn);
  }

  goToHome(): void {
    this.router.navigate(['/']);
  }

  goToRanking(): void {
    this.router.navigate(['/ranking']);
  }
}
