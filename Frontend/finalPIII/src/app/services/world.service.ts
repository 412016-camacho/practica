import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WorldList, WorldDetail } from '../models/game.model';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class WorldService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/worlds`;

  getAllWorlds(): Observable<WorldList[]> {
    return this.http.get<WorldList[]>(this.apiUrl);
  }

  getWorldById(worldId: number): Observable<WorldDetail> {
    return this.http.get<WorldDetail>(`${this.apiUrl}/${worldId}`);
  }
}
