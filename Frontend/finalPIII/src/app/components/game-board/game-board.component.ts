import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GameResponse, TurnResponse, GameStatus } from '../../models/game.model';

@Component({
  selector: 'app-game-board',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './game-board.component.html',
  styleUrl: './game-board.component.css'
})
export class GameBoardComponent {
  @Input() game: GameResponse | null = null;
  @Input() lastTurn: TurnResponse | null = null;
  @Input() loading = false;
  @Output() playTurnRequested = new EventEmitter<number | null>();

  diceOverride: number | null = null;
  useDiceOverride = false;

  onPlayTurn(): void {
    if (this.useDiceOverride && this.diceOverride) {
      this.playTurnRequested.emit(this.diceOverride);
    } else {
      this.playTurnRequested.emit(null);
    }
  }

  onDiceOverrideChange(event: Event): void {
    const checkbox = event.target as HTMLInputElement;
    this.useDiceOverride = checkbox.checked;
    if (!this.useDiceOverride) {
      this.diceOverride = null;
    }
  }

  getStatusClass(): string {
    if (!this.game) return '';
    switch (this.game.status) {
      case GameStatus.WON:
        return 'bg-green-500/20 border-green-500';
      case GameStatus.LOST:
        return 'bg-red-500/20 border-red-500';
      default:
        return 'bg-blue-500/20 border-blue-500';
    }
  }

  getStatusText(): string {
    if (!this.game) return '';
    switch (this.game.status) {
      case GameStatus.WON:
        return 'VICTORIA';
      case GameStatus.LOST:
        return 'DERROTA';
      default:
        return 'EN PROGRESO';
    }
  }

  getLifePercentage(): number {
    if (!this.game) return 0;
    return Math.max(0, Math.min(100, (this.game.life / 10) * 100));
  }

  getLifeColor(): string {
    if (!this.game) return 'bg-gray-500';
    if (this.game.life <= 2) return 'bg-red-500';
    if (this.game.life <= 5) return 'bg-yellow-500';
    return 'bg-green-500';
  }

  getPositionPercentage(): number {
    if (!this.game) return 0;
    return (this.game.position / 29) * 100;
  }

  isGameOver(): boolean {
    return this.game?.status === GameStatus.WON || this.game?.status === GameStatus.LOST;
  }

  getDeltaText(): string {
    if (!this.lastTurn) return '';
    const delta = this.lastTurn.deltaLife;
    if (delta > 0) return `+${delta}`;
    if (delta < 0) return delta.toString();
    return '0';
  }

  getDeltaClass(): string {
    if (!this.lastTurn) return '';
    if (this.lastTurn.deltaLife > 0) return 'text-green-400';
    if (this.lastTurn.deltaLife < 0) return 'text-red-400';
    return 'text-gray-400';
  }

  getCellTypeText(): string {
    if (!this.lastTurn) return '';
    switch (this.lastTurn.cell.type) {
      case 'MONSTER':
        return 'MONSTRUO';
      case 'BONUS':
        return 'BONUS';
      default:
        return 'VACIA';
    }
  }

  getCellTypeClass(): string {
    if (!this.lastTurn) return '';
    switch (this.lastTurn.cell.type) {
      case 'MONSTER':
        return 'text-red-400';
      case 'BONUS':
        return 'text-green-400';
      default:
        return 'text-gray-400';
    }
  }
}
