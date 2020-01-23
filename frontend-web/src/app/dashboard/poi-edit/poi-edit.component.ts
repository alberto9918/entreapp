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
  statusList: Array<string> = ['Open','Close'];

  constructor(private formBuilder: FormBuilder, private fb: FormBuilder, private poiService: PoiService, private categoryService: CategoryService,
    public router: Router, public snackBar: MatSnackBar, private afStorage: AngularFireStorage, private titleService: Title) { }

  ngOnInit() {
    if (this.poiService.selectedPoi == null) {
      this.router.navigate(['home']);
    } else {
      this.getData();
    }

    this.imagenForm = this.formBuilder.group({
      photo: ['']
    });

    this.titleService.setTitle('Edit - POI');
  }

  /** Get POIData from the API */
  getData() {
    this.categoryService.getAllCategories()
      .subscribe(r => this.allCategories = r);
    this.poiService.getOne(this.poiService.selectedPoi.id).subscribe(p => {
      this.poi = p;
      this.urlImage = p.images;
      console.log(p.id)
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

    this.audioguidesForm = this.fb.group({
      originalFile: [this.poi.audioguides.originalFile, Validators.compose([Validators.required])]
    });

    this.descriptionForm = this.fb.group({
      originalDescription: [this.poi.description.originalDescription, Validators.compose([Validators.required])]
    });

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
    //Subida de imagen
    const formData = new FormData();

    formData.append('photo', this.imagenForm.get('photo').value);

    if (this.poi.images.length >= 3 || this.imagenForm.get('photo').value == '') {

      //Esto ocurrirá si no se quiere editar o subir una imagen
      //Seteo los datos

      newPoi.loc = {coordinates: [this.coordinatesForm.controls['lat'].value, this.coordinatesForm.controls['lng'].value]};
      newPoi.audioguides = this.audioguidesForm.value;
      newPoi.description = this.descriptionForm.value;
      newPoi.images = this.poi.images;
      newPoi.coverImage = this.poi.coverImage;

      if (newPoi.images == null) newPoi.images = [];
      newPoi.coverImage ? null : newPoi.coverImage = newPoi.images[0];

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

        newPoi.loc = {coordinates: [this.coordinatesForm.controls['lat'].value, this.coordinatesForm.controls['lng'].value]};
        newPoi.audioguides = this.audioguidesForm.value;
        newPoi.description = this.descriptionForm.value;
        newPoi.images = this.poi.images;
        
        if (newPoi.images == null) newPoi.images = [];

        newPoi.images.push(resp.key)

        newPoi.coverImage ? null : newPoi.coverImage = newPoi.images[0];

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
  /** Function to remove an image added */
  removeImage(i) {
    this.urlImage.splice(this.urlImage.indexOf(i), 1);
  }

  deleteImage(key: String) {
    //Hay que borrar la imagen del array, y hay que borrar la imagen de amazon

    for (var i = 0; i < this.poi.images.length; i++) {
      if (this.poi.images[i] == key) {
        this.poi.images.splice(i, 1);
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
}
