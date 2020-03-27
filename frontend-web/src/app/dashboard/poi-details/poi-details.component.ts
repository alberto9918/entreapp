import { Component, OnInit } from '@angular/core';
import { MatDialog, MatSnackBar } from '@angular/material';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { DialogPoiDeleteComponent } from 'src/app/dialogs/dialog-poi-delete/dialog-poi-delete.component';
import { OnePoiResponse } from 'src/app/interfaces/one-poi-response';
import { PoiService } from 'src/app/services/poi.service';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { environment } from 'src/environments/environment';
import { LanguageService } from '../../services/language.service';
import { RatingResponse } from './../../interfaces/rating-response';
import { LanguagesResponse } from '../../interfaces/languages-response';
import { RatingService } from 'src/app/services/rating.service';
import { BarRatingModule } from "ngx-bar-rating";
import { OneRatingResponse } from 'src/app/interfaces/one-rating-response';
import { UserService } from 'src/app/services/user.service';
import { UserResponse } from 'src/app/interfaces/user-response';
import { RatingDto } from 'src/app/dto/rating.dto';

@Component({
  selector: 'app-poi-details',
  templateUrl: './poi-details.component.html',
  styleUrls: ['./poi-details.component.scss']
})
export class PoiDetailsComponent implements OnInit {

  poi: OnePoiResponse;
  coverImage: string;
  idRating: string;
  averageRating: number;
  userRating: number;
  isRated: boolean;
  languages: LanguagesResponse;
  //ratings: RatingResponse;
  //poiRatings: OneRatingResponse[];
  arrayLanguages: string[];
  arrayAudios: string[];
  arrayDescriptions: string[];
  userLogged: UserResponse;
  newRating: RatingDto;
  
  showSettings = false;
  constructor(private poiService: PoiService, public languageService: LanguageService, public router: Router,  public ratingService: RatingService,
    public dialog: MatDialog, public snackBar: MatSnackBar, private titleService: Title, public authService: AuthenticationService,
    public userService: UserService) { }

  ngOnInit() {
    if (this.poiService.selectedPoi == null) {
      this.router.navigate(['home']);
    } else {
      
      this.getData();
    }
    this.titleService.setTitle('Details - POI');
    //this.getAverageRating();
  }

  deleteRating() {
    if(window.confirm('Are you sure yo delete your rate?')){
      this.ratingService.remove(this.idRating).subscribe(resp => {
        this.loadNewRatings();
      });
      //location.reload(false);
    }
  }

  loadNewRatings() {
    this.poiService.getOne(this.poiService.selectedPoi.id).subscribe(p => {
      this.averageRating = p.averageRating;
      if(p.userRating[0]){
        this.idRating = p.userRating[0].id;
        this.userRating = p.userRating[0].rating;
      }else{
        this.idRating = '';
        this.userRating = 0;
      }
      this.isRated = p.isRated;
    })
  }

  ratingChange(e, poi){
    console.log("has entrado aqui")
    console.log(e)

    this.newRating = {user: localStorage.getItem('id'), rating: e, poi: poi.id}
    if(this.isRated == false) {
      this.ratingService.create(this.newRating).subscribe(resp => {
        console.log(resp);
        this.loadNewRatings();
      });
    }else {
      this.ratingService.edit(this.idRating, this.newRating).subscribe(resp =>{
        console.log(resp);
        this.loadNewRatings();
      });
    }
    console.log("has salido de aqui")
  }

  getAllLanguages() {
    this.languageService.getAllLanguages(this.authService.getToken()).subscribe(receivedLanguages => {
      this.languages = receivedLanguages;

      for(var i = 0; i<this.languages.rows.length; i++){
        if (this.arrayLanguages == undefined) {
          this.arrayLanguages = [this.languages.rows[i].name]
        } else {
          this.arrayLanguages.push(this.languages.rows[i].name)
        }
        for(var x = 0; x<this.poi.description.translations.length; x++){
          if(this.poi.description.translations[x].language.language == this.languages.rows[i].id){
            if(this.arrayDescriptions == undefined) {
              this.arrayDescriptions = [this.poi.description.translations[x].translatedDescription]
            }else{
              this.arrayDescriptions.push(this.poi.description.translations[x].translatedDescription)
            }
          }
        }
        if(this.arrayDescriptions == undefined || this.arrayDescriptions.length <= i){
          if(this.arrayDescriptions == undefined) {
            this.arrayDescriptions = ['']
          }else{
            this.arrayDescriptions.push('')
          }
        }
        for(var y = 0; y<this.poi.audioguides.translations.length; y++){
          if(this.poi.audioguides.translations[y].language.language == this.languages.rows[i].id){
            if(this.arrayAudios == undefined) {
              this.arrayAudios = [this.poi.audioguides.translations[y].translatedFile]
            }else {
              this.arrayAudios.push(this.poi.audioguides.translations[y].translatedFile)
            }
          }
        }
        if(this.arrayAudios == undefined || this.arrayAudios.length <= i){
          if(this.arrayAudios == undefined) {
            this.arrayAudios = ['']
          }else{
            this.arrayAudios.push('')
          }
        }
      }
    }, error => {
      this.snackBar.open('There was an error when we were loading data.', 'Close', { duration: 3000 });
    });
  }
  
  loadCoverImage(key: String) {
    return `${environment.apiUrl}/files/` + key;
  }
  loadQR(key: String) {
    return key + '?access_token='+localStorage.getItem('token');
  }                                                           
  
  loadImages(key: String) {
    
    return `${environment.apiUrl}/files/` + key;
  }
  
  checkEnglishUser() {
    const englishIsoCode = 'en';
    const userIsoCode = this.authService.getIsoCode();
    let result = false;
    if (userIsoCode == null || userIsoCode === undefined) {
      result = true;
    } else {
      result = userIsoCode.toLowerCase() === englishIsoCode.toLocaleLowerCase();

    }
    return result;

  }

  checkExistDescriptionTranslation(newPoi) {
    let posicionDescripcion = -1;

    for (let i = 0; i < newPoi.description.translations.length; i++) {
      if (newPoi.description.translations[i].id !== this.authService.getLanguageId()) {
        posicionDescripcion = i;
      }
    }
    return posicionDescripcion;
  }

  checkExistAudioTranslation(newPoi) {
    let posicionDescripcion = -1;
    for (let i = 0; i < newPoi.audioguides.translations.length; i++) {
      if (newPoi.audioguides.translations[i].id !== this.authService.getLanguageId()) {
        posicionDescripcion = i;
      }
    }
    return posicionDescripcion;
  }

  /** Api call to get all the data */
  getData() {
    this.poiService.getOne(this.poiService.selectedPoi.id).subscribe(p => {
      this.poi = p;
      
      this.averageRating = p.averageRating;
      if(p.userRating[0]){
        this.idRating = p.userRating[0].id;
        this.userRating = p.userRating[0].rating;
      }else{
        this.idRating = '';
        this.userRating = 0;
      }
      this.isRated = p.isRated;
      this.coverImage = p.coverImage;
      let posicionDescripcion = -1, posicionAudio = -1;
      let textoTraducido = '', audioTraducido = '';

      this.getAllLanguages();

      if (!this.checkEnglishUser()) {
        // obtenemos posicion del texto
        posicionDescripcion = this.checkExistDescriptionTranslation(this.poi);
        posicionAudio = this.checkExistAudioTranslation(this.poi);

        if (posicionDescripcion !== -1) {
          textoTraducido = this.poi.description.translations[posicionDescripcion].translatedDescription;
          this.poi.description.originalDescription = textoTraducido;
        }
        if (posicionAudio !== -1) {
          audioTraducido = this.poi.audioguides.translations[posicionAudio].translatedFile;
          this.poi.audioguides.originalFile = audioTraducido;
        }

        

      }

    });
    // si el usuario es de otro idioma le seteo el texto original al poi para que muestre ese
    // comprobamos si el usuario no es ingles,
    // buscamos por la id de lenguage del usuario que posicion en el poi tiene su descripcion

  }

  /** Open the view to edit a POI */
  openEditPoi() {
    this.poiService.selectedPoi = this.poi;
    this.router.navigate(['home/edit']);
  }

  /** Open the dialog to delete this POI */
  openDialogDeletePoi() {
    const dialogDeletePoi = this.dialog.open(DialogPoiDeleteComponent, { data: { poi: this.poi } });
    dialogDeletePoi.afterClosed().subscribe(res => res === 'confirm' ? this.router.navigate['/home'] : null,
      err => this.snackBar.open('There was an error when we were deleting this POI.', 'Close', { duration: 3000 }));
  }

  /** Set the image hovered as Cover (Like a carousel) */
  setAsCover(image: string) {
    this.coverImage = image;
  }


}
