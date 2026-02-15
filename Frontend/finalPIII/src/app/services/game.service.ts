import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CreateGameRequest,
  GameResponse,
  PlayTurnRequest,
  TurnResponse,
  TurnHistoryResponse
} from '../models/game.model';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/games`;

  createGame(request: CreateGameRequest): Observable<GameResponse> {
    return this.http.post<GameResponse>(this.apiUrl, request);
  }

  getGameById(gameId: number): Observable<GameResponse> {
    return this.http.get<GameResponse>(`${this.apiUrl}/${gameId}`);
  }

  playTurn(gameId: number, request?: PlayTurnRequest): Observable<TurnResponse> {
    return this.http.post<TurnResponse>(`${this.apiUrl}/${gameId}/turns`, request || {});
  }

  getTurnHistory(gameId: number, from?: number, to?: number): Observable<TurnHistoryResponse> {
    let params = new HttpParams();
    if (from !== undefined) {
      params = params.set('from', from.toString());
    }
    if (to !== undefined) {
      params = params.set('to', to.toString());
    }
    return this.http.get<TurnHistoryResponse>(`${this.apiUrl}/${gameId}/turns`, { params });
  }
}
