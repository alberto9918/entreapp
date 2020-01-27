import { Component, OnInit } from '@angular/core';
import { AngularFireStorage, AngularFireStorageReference, AngularFireUploadTask } from '@angular/fire/storage';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { PoiCreateDto } from 'src/app/dto/poi-create-dto';
import { CategoriesResponse } from 'src/app/interfaces/categories-response';
import { OnePoiResponse } from 'src/app/interfaces/one-poi-response';
import { CategoryService } from 'src/app/services/category.service';
import { PoiService } from 'src/app/services/poi.service';
import { environment } from 'src/environments/environment';
import { forEach } from '@angular/router/src/utils/collection';
import { NONE_TYPE } from '@angular/compiler/src/output/output_ast';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { LanguageService } from 'src/app/services/language.service';
import { LanguagesResponse } from 'src/app/interfaces/languages-response';

const notFoundImg = 'pois-imgs-1579797933940_notfound47.png';

@Component({
  selector: 'app-poi-edit',
  templateUrl: './poi-edit.component.html',
  styleUrls: ['./poi-edit.component.scss']
})
export class PoiEditComponent implements OnInit {

  ref: AngularFireStorageReference;
  task: AngularFireUploadTask;

  poi: OnePoiResponse;
  urlImage: Array<string> = [];
  allCategories: CategoriesResponse;
  selectedCategories = [];
  imagenForm: FormGroup;
  coordinatesForm: FormGroup;
  form: FormGroup;
  audioguidesForm: FormGroup;
  descriptionForm: FormGroup;
  translateForm: FormGroup;
  statusList: Array<string> = ['Open', 'Close'];

  languages: LanguagesResponse;

  constructor(public languageService: LanguageService, private fb: FormBuilder, private poiService: PoiService, private categoryService: CategoryService,
    public router: Router, public snackBar: MatSnackBar, private afStorage: AngularFireStorage, private titleService: Title, public authService: AuthenticationService) { }

  ngOnInit() {
    this.getAllLanguages();
    if (this.poiService.selectedPoi == null) {
      this.router.navigate(['home']);
    } else {
      this.getData();
    }

    this.imagenForm = this.fb.group({
      photo: ['']
    });
    this.audioguidesForm = this.fb.group({
      languageSelected: [null],
      translatedFile: ['']
    });


    this.titleService.setTitle('Edit - POI');
    this.getAllLanguages();
  }

  //get all languages from api
  getAllLanguages() {
    this.languageService.getAllLanguages(this.authService.getToken()).subscribe(receivedLanguages => {
      this.languages = receivedLanguages;
    }, error => {
      this.snackBar.open('There was an error when we were loading data.', 'Close', { duration: 3000 });
    });
  }

  /** Get POIData from the API */
  getData() {
    this.categoryService.getAllCategories()
      .subscribe(r => this.allCategories = r);
    this.poiService.getOne(this.poiService.selectedPoi.id).subscribe(p => {
      this.poi = p;
      this.urlImage = p.images;
      console.log(p)
      p.categories.forEach(c => this.selectedCategories.push(c.id));
      this.createForm();
    });
  }

  /** Create a form with all the data inserted */
  createForm() {
    this.coordinatesForm = this.fb.group({
      lat: [this.poi.loc.coordinates[0], Validators.compose([Validators.required])],
      lng: [this.poi.loc.coordinates[1], Validators.compose([Validators.required])]
    });

    this.translateForm = this.fb.group({
      languageSelected: [null],
      translateDescripcion: [null]
    });

    /*this.descriptionForm = this.fb.group({
      originalDescription: [this.poi.description.originalDescription, Validators.compose([Validators.required])]
    });*/

    this.form = this.fb.group({
      name: [this.poi.name, Validators.compose([Validators.required])],
      year: [this.poi.year, Validators.compose([Validators.required])],
      creator: [this.poi.creator],
      coverImage: [this.poi.coverImage],
      /*images: [this.poi.images, Validators.compose([Validators.required])]*/
      categories: [this.selectedCategories, Validators.compose([Validators.required])],
      status: [this.poi.status, Validators.compose([Validators.required])],
      schedule: [this.poi.schedule, Validators.compose([Validators.required])],
      price: [this.poi.price],
    });
  }

  /** Send all the data to the api */
  onSubmit() {

    const newPoi: PoiCreateDto = <PoiCreateDto>this.form.value;
    //newPoi.images = this.imagesForm.value;

    //Subida de imagen
    const formData = new FormData();
    //Subida de audio
    const formData2 = new FormData();

    formData.append('photo', this.imagenForm.get('photo').value);
    //COMENTADO PORQUE ESTÁ EN DESARROLLO EL AUDIO
    formData2.append('audio', this.audioguidesForm.controls['translatedFile'].value);

    if (this.poi.images.length >= 3 || this.imagenForm.get('photo').value == '') {

      //Esto ocurrirá si no se quiere editar o subir una imagen
      //Seteo los datos

      newPoi.loc = { coordinates: [this.coordinatesForm.controls['lat'].value, this.coordinatesForm.controls['lng'].value] };
      //newPoi.audioguides = this.audioguidesForm.value;
      //newPoi.description = this.descriptionForm.value;
      newPoi.description = this.poi.description;
      newPoi.description.translations = this.poi.description.translations;

      if (this.poi.images[0] != null) {
        newPoi.images = this.poi.images;
      }

      newPoi.coverImage = this.poi.coverImage;

      if (newPoi.images == null) newPoi.images = [];
      //newPoi.coverImage ? null : newPoi.coverImage = newPoi.images[0]; Esto no haría falta pero lo dejo por si a caso

      //Comprobación de existencia de traducciones
      if (this.poi.description.translations == undefined) {
        console.log('this.poi.descriptions.translations indefinido')
        newPoi.description.translations = [{
          language: {
            language: this.translateForm.controls['languageSelected'].value
          },
          translatedDescription: this.translateForm.controls['translateDescripcion'].value
        }];
      } else {
        /*for(var i=0; i<newPoi.description.translations.length; i++) {
            
          }*/
        var buscarIdioma = null;
        buscarIdioma = newPoi.description.translations.find(element => element.language.language == this.translateForm.controls['languageSelected'].value);

        if (buscarIdioma != undefined) {
          console.log(buscarIdioma);

          newPoi.description.translations.splice(newPoi.description.translations.indexOf(buscarIdioma), 1, ({
            language: {
              language: this.translateForm.controls['languageSelected'].value
            },
            translatedDescription: this.translateForm.controls['translateDescripcion'].value
          }));

          /*this.translateForm = this.fb.group({
            languageSelected: [
              this.translateForm.controls['languageSelected'].value
            ],
            translateDescripcion: [
              newPoi.description.translations[i].translatedDescription = this.translateForm.controls['translateDescripcion'].value
            ]
          });*/

        } else {
          console.log('CREA NUEVO IDIOMA EN EL ARRAY');
          newPoi.description.translations.push({
            language: {
              language: this.translateForm.controls['languageSelected'].value
            },
            translatedDescription: this.translateForm.controls['translateDescripcion'].value
          });
        }
      }

      if (this.poi.coverImage == undefined && newPoi.images.length > 0) {
        newPoi.coverImage = newPoi.images[0];
      }

      if (this.audioguidesForm.get('translatedFile').value == '') {
        console.log('No introduces nada pisha')
        console.log(this.audioguidesForm.get('translatedFile').value)
        newPoi.audioguides = this.poi.audioguides;


      } else {
        //Cuando introduzcamos un audio
        this.poiService.uploadAudio(formData2).subscribe(resp => {
          newPoi.audioguides = this.poi.audioguides;
          console.log(newPoi.audioguides)
          /*Dado que van a haber varios idiomas creo que es más 
          apropiado realizar el tratamiento del idioma de la audio guia mediante un switch
          Lo variable que le pasamos al switch debe ser en realidad un variable que recojamos del 
          formulario*/
          //this.audioguidesForm.get('languageSelected')


          if (this.poi.audioguides.translations == undefined) {
            console.log('El array esta undefinido')
            console.log('this.poi.audioguides.translations indefinido')
            newPoi.audioguides.translations = [{
              language: {
                language: this.audioguidesForm.controls['languageSelected'].value
              },
              translatedFile: resp.key
            }];
          } else {
            console.log('El array esta definido')
            var buscarIdioma = null;
            buscarIdioma = newPoi.audioguides.translations.find(element => element.language.language == this.audioguidesForm.controls['languageSelected'].value);


            if (buscarIdioma != undefined) {
              console.log('Ha encontrado otro idima')
              console.log(buscarIdioma);

              newPoi.audioguides.translations.splice(newPoi.audioguides.translations.indexOf(buscarIdioma), 1, ({
                language: {
                  language: this.audioguidesForm.controls['languageSelected'].value
                },
                translatedFile: resp.key
              }));

            } else {
              //console.log('CREA NUEVO IDIOMA EN EL ARRAY DE AUDIOGUIAS'+ resp.key);
              //console.log(newPoi.audioguides.translations)
              //console.log('-----------------------------------------')


              newPoi.audioguides.translations.push({
                language: {
                  language: this.audioguidesForm.controls['languageSelected'].value
                },
                translatedFile: resp.key
              });
              console.log(newPoi.audioguides.translations)
            }
          }



          this.poiService.edit(this.poi.id, newPoi).subscribe(resp => {
            this.router.navigate(['/home']);
          }, error => {
            this.snackBar.open('Error editing the POI', 'Close', { duration: 3000 })
          });

        }, error => {
          console.log(error);
        })
      }

      //EDICIÓN

      this.poiService.edit(this.poi.id, newPoi).subscribe(resp => {
        this.router.navigate(['/home']);
      }, error => {
        this.snackBar.open('Error editing the POI', 'Close', { duration: 3000 })
      });




    } else {

      //Esto ocurrirá si se quiere subir una imagen

      //Subo la imagen y recojo su key
      this.poiService.uploadImage(formData).subscribe(resp => {

        //Seteo los datos

        newPoi.loc = { coordinates: [this.coordinatesForm.controls['lat'].value, this.coordinatesForm.controls['lng'].value] };
        //newPoi.audioguides = this.audioguidesForm.value;
        //newPoi.description = this.descriptionForm.value;
        newPoi.coverImage = this.poi.coverImage;
        newPoi.description = this.poi.description;
        newPoi.description.translations = this.poi.description.translations;

        //TESTEANDO
        if (this.poi.images[0] != null) {
          newPoi.images = this.poi.images;
        }
        if (newPoi.images == null) newPoi.images = [];

        //Comprobación de existencia de traducciones
        if (this.poi.description.translations == undefined) {
          console.log('this.poi.descriptions.translations indefinido')
          newPoi.description.translations = [{
            language: {
              language: this.translateForm.controls['languageSelected'].value
            },
            translatedDescription: this.translateForm.controls['translateDescripcion'].value
          }];
        } else {
          /*for(var i=0; i<newPoi.description.translations.length; i++) {
              
            }*/
          var buscarIdioma = null;
          buscarIdioma = newPoi.description.translations.find(element => element.language.language == this.translateForm.controls['languageSelected'].value);

          if (buscarIdioma != undefined) {
            console.log(buscarIdioma);

            newPoi.description.translations.splice(newPoi.description.translations.indexOf(buscarIdioma), 1, ({
              language: {
                language: this.translateForm.controls['languageSelected'].value
              },
              translatedDescription: this.translateForm.controls['translateDescripcion'].value
            }));






          } else {
            console.log('CREA NUEVO IDIOMA EN EL ARRAY');
            newPoi.description.translations.push({
              language: {
                language: this.translateForm.controls['languageSelected'].value
              },
              translatedDescription: this.translateForm.controls['translateDescripcion'].value
            });
          }
        }

        newPoi.images.push(resp.key)

        newPoi.coverImage ? null && newPoi.images[0] != null || newPoi.coverImage == notFoundImg && newPoi.images[0] != null : newPoi.coverImage = newPoi.images[0];




        if (this.audioguidesForm.get('translatedFile').value == '') {
          console.log('No introduces nada pisha')
          console.log(this.audioguidesForm.get('translatedFile').value)
          newPoi.audioguides = this.poi.audioguides;


        } else {
          //Cuando introduzcamos un audio
          console.log(formData2)
          this.poiService.uploadAudio(formData2).subscribe(resp => {
            newPoi.audioguides = this.poi.audioguides;
            /*Dado que van a haber varios idiomas creo que es más 
            apropiado realizar el tratamiento del idioma de la audio guia mediante un switch
            Lo variable que le pasamos al switch debe ser en realidad un variable que recojamos del 
            formulario*/
            //this.audioguidesForm.get('languageSelected')


            if (this.poi.audioguides.translations == undefined) {
              console.log('El array esta undefinido')
              console.log('this.poi.audioguides.translations indefinido')
              newPoi.audioguides.translations = [{
                language: {
                  language: this.audioguidesForm.controls['languageSelected'].value
                },
                translatedFile: resp.key
              }];
            } else {
              console.log('El array esta definido')
              var buscarIdioma = null;
              buscarIdioma = newPoi.audioguides.translations.find(element => element.language.language == this.audioguidesForm.controls['languageSelected'].value);


              if (buscarIdioma != undefined) {
                console.log('Ha encontrado otro idima')
                console.log(buscarIdioma);

                newPoi.audioguides.translations.splice(newPoi.audioguides.translations.indexOf(buscarIdioma), 1, ({
                  language: {
                    language: this.audioguidesForm.controls['languageSelected'].value
                  },
                  translatedFile: resp.key
                }));

              } else {
                console.log('CREA NUEVO IDIOMA EN EL ARRAY DE AUDIOGUIAS' + resp.key);
                console.log(resp.key + 'papa')
                newPoi.audioguides.translations.push({
                  language: {
                    language: this.audioguidesForm.controls['languageSelected'].value
                  },
                  translatedFile: resp.key
                });
              }
            }

            this.poiService.edit(this.poi.id, newPoi).subscribe(resp => {
              this.router.navigate(['/home']);
            }, error => {
              this.snackBar.open('Error editing the POI', 'Close', { duration: 3000 })
            });



          }, error => {
            console.log(error);
          })
        }
        //EDICIÓN

        this.poiService.edit(this.poi.id, newPoi).subscribe(resp => {
          this.router.navigate(['/home']);
        }, error => {
          this.snackBar.open('Error editing the POI', 'Close', { duration: 3000 })
        });

      }, error => {
        console.log(error);
      })
    }
    //Comporbaciones para el audio

    //Si no queremos subir ningún audio


  }


  /*     newPoi.loc = {coordinates: [this.coordinatesForm.controls['lat'].value, this.coordinatesForm.controls['lng'].value]};
      newPoi.audioguides = this.audioguidesForm.value;
      newPoi.description = this.descriptionForm.value;
      //Con esta línea siempre va a haber una única imagen por monumento
      newPoi.images = [];
      newPoi.images.push(imgKey);
      //newPoi.coverImage = imgKey;
  
      //newPoi.coverImage ? null : newPoi.coverImage = newPoi.images[0];
      this.poiService.edit(this.poi.id, newPoi).subscribe(resp => {
        this.router.navigate(['/home']);
        console.log('Se ha "editado"')
      }, error => {
        this.snackBar.open('Error editing the POI', 'Close', { duration: 3000 })
      })
       */


  /** Function to change the translate description*/
  translateChange(e) {
    var searchLanguage;
    searchLanguage = this.poi.description.translations.find(element => element.language.language == e.value);

    if (searchLanguage != undefined) {
      console.log(searchLanguage);
      this.translateForm = this.fb.group({
        languageSelected: [searchLanguage.language.language],
        translateDescripcion: [
          /*this.poi.description.translations[i].translatedDescription*/
          searchLanguage.translatedDescription
        ]
      });
    } else {
      console.log('searchLanguage indefido')
      this.translateForm = this.fb.group({
        languageSelected: [e.value],
        translateDescripcion: [null]
      });

    }
  }
  //lomismoquearribaperoparaelaudio

  audioChange(e) {
    var searchLanguage;
    searchLanguage = this.poi.audioguides.translations.find(element => element.language.language == e.value);

    if (searchLanguage != undefined) {
      console.log(searchLanguage);
      this.audioguidesForm = this.fb.group({
        languageSelected: [searchLanguage.language.language],
        translatedFile: [
          /*this.poi.description.translations[i].translatedDescription*/
          searchLanguage.translatedFile
        ]
      });
    } else {
      console.log('searchLanguage indefido')
      this.audioguidesForm = this.fb.group({
        languageSelected: [e.value],
        translatedFile: ['']
      });

    }
  }

  /** Function to upload multiple images to Firebase-Firestorage */
  ImgUpload(e) {
    for (let i = 0; i < e.target.files.length; i++) {
      const timestamp = new Date().getTime();
      const id = Math.random().toString(36).substring(2);
      const file = e.target.files[i];
      const filePath = `img/poi/${id}-${timestamp}`;
      const ref = this.afStorage.ref(filePath);
      const task = this.afStorage.upload(filePath, file);

      task.snapshotChanges().pipe(
        finalize(() => ref.getDownloadURL().subscribe(r => {
          this.urlImage.push(r);
          this.form.controls['images'].setValue(this.urlImage);
        }))).subscribe();
    }
  }

  /** Function to upload only one audioguide to Firebase-Firestorage */
  audioUpload(e) {
    const randomId = Math.random().toString(36).substring(2);
    const timeId = new Date().getTime();
    const file = e.target.files[0];
    const filePath = `audioguides/${randomId}-${timeId}`;
    const ref = this.afStorage.ref(filePath);
    const task = this.afStorage.upload(filePath, file);

    task.snapshotChanges().pipe(
      finalize(() => ref.getDownloadURL().subscribe(r => this.audioguidesForm.controls['originalFile'].setValue(r)))).subscribe();
  }
  loadImages(key: String) {
    return `${environment.apiUrl}/files/` + key;
  }

  deleteImage(key: String) {
    //Hay que borrar la imagen del array, y hay que borrar la imagen de amazon

    for (var i = 0; i < this.poi.images.length; i++) {
      if (this.poi.images[i] == key) {
        this.poi.images.splice(i, 1);
        if (key == this.poi.coverImage) {
          this.poi.coverImage = notFoundImg;
        }
      }
    }

    this.poiService.removeImage(key).subscribe(resp => {
      console.log(resp)
    }, error => {
      console.log(error)
    })

  }

  selectAsCover(i: string) {
    this.poi.coverImage = i;
  }

  comprobarImagen(i: String) {
    if (i == this.poi.coverImage) {
      return true
    } else {
      return false
    }
  }

  onImageSelect(event) {

    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      this.imagenForm.get('photo').setValue(file);
      console.log("ta lleno")
    } else {
      console.log("ta vacío")
    }
  }

  onAudioSelect(event) {

    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      console.log(file);

      this.audioguidesForm.get('translatedFile').setValue(file);
      console.log("ta lleno")
    } else {
      console.log("ta vacío")
    }
  }
}
