import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  WorldList,
  WorldDetail,
  CreateGameRequest,
  GameResponse,
  PlayTurnRequest,
  TurnResponse,
  TurnHistoryResponse,
  RankingResponse
} from '../models/game.model';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class GameApiService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  // Worlds
  getAllWorlds(): Observable<WorldList[]> {
    return this.http.get<WorldList[]>(`${this.baseUrl}/worlds`);
  }

  getWorldById(worldId: number): Observable<WorldDetail> {
    return this.http.get<WorldDetail>(`${this.baseUrl}/worlds/${worldId}`);
  }

  // Games
  createGame(request: CreateGameRequest): Observable<GameResponse> {
    return this.http.post<GameResponse>(`${this.baseUrl}/games`, request);
  }

  getGameById(gameId: number): Observable<GameResponse> {
    return this.http.get<GameResponse>(`${this.baseUrl}/games/${gameId}`);
  }

  playTurn(gameId: number, request?: PlayTurnRequest): Observable<TurnResponse> {
    return this.http.post<TurnResponse>(`${this.baseUrl}/games/${gameId}/turns`, request || {});
  }

  getTurnHistory(gameId: number, from?: number, to?: number): Observable<TurnHistoryResponse> {
    let params = new HttpParams();
    if (from !== undefined) {
      params = params.set('from', from.toString());
    }
    if (to !== undefined) {
      params = params.set('to', to.toString());
    }
    return this.http.get<TurnHistoryResponse>(`${this.baseUrl}/games/${gameId}/turns`, { params });
  }

  // Ranking
  getRanking(top: number = 10): Observable<RankingResponse> {
    const params = new HttpParams().set('top', top.toString());
    return this.http.get<RankingResponse>(`${this.baseUrl}/ranking`, { params });
  }
}
