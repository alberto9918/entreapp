import { OneRatingResponse } from './one-rating-response';

export interface RatingResponse {
    count: number;
    rows: OneRatingResponse[];
}
