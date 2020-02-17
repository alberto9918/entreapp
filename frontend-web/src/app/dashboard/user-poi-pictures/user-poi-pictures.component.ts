import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { PoiService } from 'src/app/services/poi.service';
import { MatDialog, MatSnackBar } from '@angular/material';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { UserImagesResponse } from 'src/app/interfaces/user-images-response';

@Component({
  selector: 'app-user-poi-pictures',
  templateUrl: './user-poi-pictures.component.html',
  styleUrls: ['./user-poi-pictures.component.scss']
})
export class UserPoiPicturesComponent implements OnInit {

  imagesUser: UserImagesResponse[];
  idImages : String[];

  constructor(private userService: UserService,private poiService: PoiService, public dialog: MatDialog,
    public router: Router, public snackBar: MatSnackBar, private titleService: Title, public authService: AuthenticationService) { }

  ngOnInit() {
    this.getPhotos();
    this.idImages = new Array<String>();


  }

  getPhotos(){


    this.imagesUser = new Array<UserImagesResponse>();
    this.userService.selectedUser.images.forEach(element => {
      if(element.poi.toString() == this.poiService.selectedPoi.id)
          this.imagesUser.push(element);
          console.log(element._id)

    });


  }

  addArray(event, p){
    
    
    console.log(this.idImages + "buenos dias")
    console.log(event.currentTarget.checked);
   if(event.currentTarget.checked == true){
      this.idImages.push(p._id)
      console.log(this.idImages)
    }else{
      this.idImages.forEach(element => {

        if (element == p._id){
          this.idImages.splice(this.idImages.indexOf(p),1);
          console.log(this.idImages)

        }
        
      });
    }
  }



}
