import { OnePoiResponse } from "../interfaces/one-poi-response";

export class UserImageInvalidDto{
    _id: String;
    poi: OnePoiResponse;
    thumbnail: String;
    full: String;
    dateToBeRemoved: Date;


    constructor(_id: string, poi: OnePoiResponse, thumbnail: String, full: String, dateToBeRemoved: Date) {
        this._id = _id;
        this.poi = poi;
        this.thumbnail = thumbnail;
        this.full = full;
        this.dateToBeRemoved = dateToBeRemoved;


    }

}