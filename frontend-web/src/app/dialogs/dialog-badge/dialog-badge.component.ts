import { Component, Inject, OnInit } from '@angular/core';
import { AngularFireStorage, AngularFireStorageReference, AngularFireUploadTask } from '@angular/fire/storage';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { MatSnackBar } from '@angular/material/snack-bar';
import { finalize } from 'rxjs/operators';
import { OnePoiResponse } from 'src/app/interfaces/one-poi-response';

import { BadgeDto } from './../../dto/badge.dto';
import { BadgeService } from './../../services/badge.service';
import { PoiService } from './../../services/poi.service';

@Component({
  selector: 'app-dialog-badge',
  templateUrl: './dialog-badge.component.html',
  styleUrls: ['./dialog-badge.component.scss']
})
export class DialogBadgeComponent implements OnInit {
  edit: boolean;
  name: string;
  points: number;
  description: string;
  icon: string;
  pois: OnePoiResponse[];
  allPois: OnePoiResponse[];
  selectedPois = [];
  badgeId: string;
  form: FormGroup;
  ref: AngularFireStorageReference;
  task: AngularFireUploadTask;
  urlImage: string;

  constructor(private afStorage: AngularFireStorage, private snackBar: MatSnackBar, private fb: FormBuilder, @Inject(MAT_DIALOG_DATA) public data: any, private poisService: PoiService, private badgeService: BadgeService, public dialogRef: MatDialogRef<DialogBadgeComponent>) { }

  ngOnInit() {
    this.getAllPois();
    this.createForm();
    if (this.data.badge) {
      this.edit = true;
      this.badgeId = this.data.badge.id;
      this.urlImage = this.data.badge.icon;
    } else {
      this.edit = false;
    }
  }

  onSubmit() {
    if (this.edit) {
      const editedBadge: BadgeDto = <BadgeDto>this.form.value;
      this.badgeService.edit(this.badgeId, editedBadge).subscribe(result => {
        this.dialogRef.close('confirm');
      }, error => this.snackBar.open('There was an error when were trying to edit this badge.', 'Close', { duration: 3000 }));
    } else {
      const newBadge: BadgeDto = <BadgeDto>this.form.value;
      this.badgeService.create(newBadge).subscribe(result => {
        this.dialogRef.close('confirm');
      }, error => this.snackBar.open('There was an error when were trying to create this badge.', 'Close', { duration: 3000 }));
    }
  }

  createForm() {
    if (this.data) {
      const editForm: FormGroup = this.fb.group({
        name: [this.data.badge.name, Validators.compose([Validators.required])],
        points: [this.data.badge.points, Validators.compose([Validators.required])],
        description: [this.data.badge.description, Validators.compose([Validators.required])],
        icon: [this.data.badge.icon, Validators.compose([Validators.required])],
        pois: [this.selectedPois, Validators.compose([Validators.required])]
      });
      this.form = editForm;
    } else {
      const newForm: FormGroup = this.fb.group({
        name: [null, Validators.compose([Validators.required])],
        points: [null, Validators.compose([Validators.required])],
        description: [null, Validators.compose([Validators.required])],
        icon: [null, Validators.compose([Validators.required])],
        pois: [null, Validators.compose([Validators.required])]
      });
      this.form = newForm;
    }
  }

  getAllPois() {
    this.poisService.getAll().subscribe(
      pois => {
        this.allPois = pois.rows;
        if (this.data.badge) {
          this.data.badge.pois.forEach(p => {
            this.selectedPois.push(p.id);
          });
        }
      }, error => this.snackBar.open('There was an error when were loading data.', 'Close', { duration: 3000 }));
  }

  addBadge() {
    const poisIds: string[] = [];
    this.pois.forEach(poi => {
      poisIds.push(poi.id);
    });
    const badgeCreateDto = new BadgeDto(this.name, this.points, this.description, this.icon, poisIds);
    this.badgeService.create(badgeCreateDto).subscribe(
      badge => {
        this.dialogRef.close('confirm');
      }
    );
  }

  upload(e) {
    const id = Math.random().toString(36).substring(2);
    const file = e.target.files[0];
    const filePath = `${e.target.files[0].type}/${id}`;
    const ref = this.afStorage.ref(filePath);
    const task = this.afStorage.upload(filePath, file);

    task.snapshotChanges().pipe(finalize(() =>
      ref.getDownloadURL().subscribe(r => {
        this.urlImage = r;
        this.form.controls['icon'].setValue(this.urlImage);
      }))).subscribe();
  }

  editBadge() {
    const poisIds: string[] = [];
    this.pois.forEach(poi => {
      poisIds.push(poi.id);
    });
    const badgeCreateDto = new BadgeDto(this.name, this.points, this.description, this.icon, poisIds);
    this.badgeService.edit(this.badgeId, badgeCreateDto).subscribe(
      badge => {
        this.dialogRef.close('confirm');
      }
    );
  }


}
