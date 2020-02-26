import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { PoiService } from 'src/app/services/poi.service';
import { MatDialog, MatSnackBar } from '@angular/material';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { UserImagesResponse } from 'src/app/interfaces/user-images-response';
import { UserImagesInvalidResponse } from 'src/app/interfaces/user-invalida-images-response';
import { UserImageInvalidDto } from 'src/app/dto/user.image.invalid.dto';

@Component({
  selector: 'app-user-poi-pictures',
  templateUrl: './user-poi-pictures.component.html',
  styleUrls: ['./user-poi-pictures.component.scss']
})
export class UserPoiPicturesComponent implements OnInit {

  imagesUser: UserImagesResponse[];
  imageInvalid : UserImageInvalidDto;
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
          this.idImages.splice(this.idImages.indexOf(element),1);
          console.log(this.idImages)

        }
        
      });
    }
  }

  updateUser(){
    
    this.idImages.forEach(i => {
      console.log("Array de bucle")
        
        this.userService.selectedUser.images.forEach(element => {
          console.log(this.idImages.indexOf(i) + " Macundra")
          console.log(this.userService.selectedUser.images.indexOf(element) + " pepe");
          if(element._id == i ){
            console.log(this.idImages.indexOf(i) + " MacundraSupremo")
            console.log(this.userService.selectedUser.images.indexOf(element) + " pepeSupremo");
            console.log(this.userService.selectedUser.images.splice(this.userService.selectedUser.images.indexOf(element),1));
            
            this.imageInvalid = new UserImageInvalidDto("",element.poi, "","",new Date(Date.now()));


            this.imageInvalid._id = element._id
            this.imageInvalid.dateToBeRemoved.setDate( this.imageInvalid.dateToBeRemoved.getDate() + 15)
            this.imageInvalid.full = element.full
            this.imageInvalid.thumbnail = element.thumbnail
            

            this.userService.selectedUser.invalidImages.push(this.imageInvalid)
          }
        });
       

    });

    
    console.log(this.userService.selectedUser)
    this.userService.editMyProfile(this.userService.selectedUser ,this.userService.selectedUser.id.toString())
    .subscribe(r => this.snackBar.open('User updated successfully.', 'Close', { duration: 3000 }),
      e => this.snackBar.open('Failed to update user.', 'Close', { duration: 3000 }));


    this.router.navigate(['home/user/photos'])
  }



}
