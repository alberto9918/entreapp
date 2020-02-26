import { OnePoiResponse } from "./one-poi-response";

export interface UserResponse {
    id: number;
    name: string;
    role: string;
    picture: string;
    images: [{
        _id: String;
        poi: OnePoiResponse,
        thumbnail: String,
        full: String,
    }];
    invalidImages:[{
        _id: String;
        poi: OnePoiResponse,
        thumbnail: String,
        full: String,
        dateToBeRemoved: Date
    }];
    password: string;
    email: string;
    createAt: string;
    country: string;
    language:string;
    badges: {
        id: string;
        points: number
    };
}
