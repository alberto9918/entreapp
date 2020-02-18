export class UserImageInvalidDto{
    _id: String;
    thumbnail: String;
    full: String;
    dateToBeRemoved: Date;


    constructor(_id: string,thumbnail: String, full: String, dateToBeRemoved: Date) {
        this._id = _id;
        this.thumbnail = thumbnail;
        this.full = full;
        this.dateToBeRemoved = dateToBeRemoved;


    }

}