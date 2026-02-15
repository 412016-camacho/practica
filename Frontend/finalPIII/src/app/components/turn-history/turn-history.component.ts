import { Component, Input, Output, EventEmitter } from '@angular/core';
import { TurnHistoryItem, GameStatus } from '../../models/game.model';

@Component({
  selector: 'app-turn-history',
  standalone: true,
  templateUrl: './turn-history.component.html',
  styleUrl: './turn-history.component.css'
})
export class TurnHistoryComponent {
  @Input() turns: TurnHistoryItem[] = [];
  @Input() gameId: number | null = null;
  @Output() refreshRequested = new EventEmitter<void>();
  @Output() turnSelected = new EventEmitter<TurnHistoryItem>();

  onRefresh(): void {
    this.refreshRequested.emit();
  }

  onTurnClick(turn: TurnHistoryItem): void {
    this.turnSelected.emit(turn);
  }

  getStatusClass(status: GameStatus): string {
    switch (status) {
      case GameStatus.WON:
        return 'text-green-400';
      case GameStatus.LOST:
        return 'text-red-400';
      default:
        return 'text-blue-400';
    }
  }

  getDeltaClass(delta: number): string {
    if (delta > 0) return 'text-green-400';
    if (delta < 0) return 'text-red-400';
    return 'text-gray-400';
  }

  formatDelta(delta: number): string {
    if (delta > 0) return `+${delta}`;
    return delta.toString();
  }
}
