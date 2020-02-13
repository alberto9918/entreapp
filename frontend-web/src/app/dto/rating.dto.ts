export class RatingDto {
    user: string;
    rating: number;
    poi: string;
    constructor(user: string, rating: number, poi: string) {
        this.user = user;
        this.rating = rating;
        this.poi = poi;
    }
}