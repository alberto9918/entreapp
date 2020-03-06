import { OnePoiResponse } from "./one-poi-response";

export interface UserImagesInvalidResponse {

    _id: String;
    poi: OnePoiResponse;
    thumbnail: String;
    full: String;
    dateToBeRemoved: Date;


}