import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { MatDialog, MatSnackBar } from '@angular/material';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { OnePoiResponse } from 'src/app/interfaces/one-poi-response';
import { PoiService } from 'src/app/services/poi.service';
import { PoiResponse } from 'src/app/interfaces/poi-response';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-user-pictures',
  templateUrl: './user-pictures.component.html',
  styleUrls: ['./user-pictures.component.scss']
})
export class UserPicturesComponent implements OnInit {

  POIs: PoiResponse;
  arrayPois : OnePoiResponse[];
  cont:number;
  

  constructor(private userService: UserService,private poiService: PoiService, public dialog: MatDialog,
    public router: Router, public snackBar: MatSnackBar, private titleService: Title, public authService: AuthenticationService) { }

  ngOnInit() {
    console.log("Buenosdias")
    console.log( this.userService.selectedUser.name)

    this.arrayPois = new Array<OnePoiResponse>();
    this.getAll();
/*
      this.POIs.rows.forEach(e => {
         this.cont = 0;
       this.userService.selectedUser.images.forEach(element =>{
        if(e.id == element.poi.id){
         this.cont++; 
        }
       })
       if(this.cont == 0){
        this.POIs.rows.splice(this.POIs.rows.indexOf(e),1);
       }
      });*/


    
    

      
    


  }

  getAll() {
    this.poiService.getAll().subscribe(receivedPois =>{


      receivedPois.rows.forEach(element => {
        console.log("COntador0")

        this.cont =0
        this.userService.selectedUser.images.forEach(e => {
          console.log("images")
          console.log(e.poi.toString())
          console.log("POI")
          console.log(element.id)


          
          if(element.id == e.poi.toString()){
            console.log("mas uno")

            this.cont++
          }
        
        })
        if(this.cont != 0){
          console.log("añodo")
          this.arrayPois.push(element)

        }
      

        
      });

    })
      
      
      
      
      ,
      err => this.snackBar.open('There was an error when we were loading data.', 'Close', { duration: 3000 });
  }

  loadCoverImage(key: String) {
    return `${environment.apiUrl}/files/` + key;
  }


  

}
