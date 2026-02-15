import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RankingResponse } from '../models/game.model';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class RankingService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/ranking`;

  getRanking(top: number = 10): Observable<RankingResponse> {
    const params = new HttpParams().set('top', top.toString());
    return this.http.get<RankingResponse>(this.apiUrl, { params });
  }
}
