import { Component, OnInit } from '@angular/core';
import { AngularFireStorage, AngularFireStorageReference, AngularFireUploadTask } from '@angular/fire/storage';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { PoiCreateDto } from 'src/app/dto/poi-create-dto';
import { CategoriesResponse } from 'src/app/interfaces/categories-response';
import { CategoryService } from 'src/app/services/category.service';
import { PoiService } from 'src/app/services/poi.service';
import { LanguageService } from 'src/app/services/language.service';
import { AuthenticationService } from 'src/app/services/authentication.service';


@Component({
  selector: 'app-poi-create',
  templateUrl: './poi-create.component.html',
  styleUrls: ['./poi-create.component.scss']
})
export class PoiCreateComponent implements OnInit {
  ref: AngularFireStorageReference;
  task: AngularFireUploadTask;
  urlImage: Array<string> = [];
  urlAudioguide: string;
  spanishLanguage: string;

  poi: PoiCreateDto;
  allCategories: CategoriesResponse;

  coordinatesForm: FormGroup;
  form: FormGroup;
  audioguidesForm: FormGroup;
  descriptionForm: FormGroup;
  statusList: Array<string> = ['Open','Close'];

  constructor(private fb: FormBuilder, private poiService: PoiService, private categoryService: CategoryService, private authService: AuthenticationService,
    public router: Router, public snackBar: MatSnackBar, private afStorage: AngularFireStorage, private titleService: Title, private languageService: LanguageService) { }

  ngOnInit() {
    this.createForm();
    this.getCategories();
    this.getSpanishLanguage();
    this.titleService.setTitle('Create - POI');
  }

  /** Get all categories to show them */
  getCategories() {
    this.categoryService.getAllCategories()
      .subscribe(r => this.allCategories = r);
  }

  getSpanishLanguage() {
    this.languageService.getSpanishLanguage(this.authService.getToken())
      .subscribe(r => this.spanishLanguage = r.id);
  }

  /** Create an empty form */
  createForm() {
    this.coordinatesForm = this.fb.group({
      lat: [null, Validators.compose([Validators.required])],
      lng: [null, Validators.compose([Validators.required])]
    });

    this.audioguidesForm = this.fb.group({
      originalFile: [null, Validators.compose([Validators.required])]
    });

    this.descriptionForm = this.fb.group({
      originalDescription: [null, Validators.compose([Validators.required])]
    });
    this.form = this.fb.group({
      name: [null, Validators.compose([Validators.required])],
      year: [null, Validators.compose([Validators.required])],
      creator: [localStorage.getItem('name')],
      //images: [null, Validators.compose([Validators.required])],
      categories: [null, Validators.compose([Validators.required])],
      status: [null, Validators.compose([Validators.required])],
      schedule: [null, Validators.compose([Validators.required])],
      price: [null],
    });
  }

  /** Send all the data to the api */
  onSubmit() {
    const newPoi: PoiCreateDto = <PoiCreateDto>this.form.value;
    newPoi.loc = { coordinates: [this.coordinatesForm.controls['lat'].value, this.coordinatesForm.controls['lng'].value] };
    //newPoi.audioguides = this.audioguidesForm.value;
    newPoi.description = this.descriptionForm.value;
    //TODO id del idioma español
    //newPoi.description.language.language = this.spanishLanguage;
    newPoi.description.language = { language: this.spanishLanguage};

    console.log(newPoi);


    this.poiService.create(newPoi).subscribe(() => {
      this.router.navigate(['/home']);
    }, error => {
      this.snackBar.open('Error creating the POI.', 'Close', { duration: 3000 });
    });
  }

  /** Function to upload multiple images to Firebase-Firestorage */
  // TODO Cambiar para que haga una llamada al servicio de subida de ficheros
  ImgUpload(e) {
    for (let i = 0; i < e.target.files.length; i++) {
      const id = Math.random().toString(36).substring(2);
      const file = e.target.files[i];
      const filePath = `img/poi/${id}`;
      const ref = this.afStorage.ref(filePath);
      const task = this.afStorage.upload(filePath, file);

      task.snapshotChanges().pipe(
        finalize(() => ref.getDownloadURL()
          .subscribe(r => {
            this.urlImage.push(r);
            this.form.controls['images'].setValue(this.urlImage);
          })))
        .subscribe();
    }
  }

  /** Function to upload only one audioguide to Firebase-Firestorage */
  // TODO Cambiar para que haga una llamada al servicio de subida de ficheros

  audioUpload(e) {
    const id = Math.random().toString(36).substring(2);
    const file = e.target.files[0];
    const filePath = `audioguides/${id}`;
    const ref = this.afStorage.ref(filePath);
    const task = this.afStorage.upload(filePath, file);

    task.snapshotChanges().pipe(
      finalize(() => ref.getDownloadURL().subscribe(r => this.audioguidesForm.controls['originalFile'].setValue(r)))).subscribe();
  }

  /** Function to remove an image added */
  removeImage(i) {
    this.urlImage.splice(this.urlImage.indexOf(i), 1);
  }

}
