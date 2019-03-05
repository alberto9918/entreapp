import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatSnackBar } from '@angular/material';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-dialog-delete-user',
  templateUrl: './dialog-delete-user.component.html',
  styleUrls: ['./dialog-delete-user.component.scss']
})
export class DialogDeleteUserComponent implements OnInit {

  public form: FormGroup;
  checkedRobot: boolean;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private fb: FormBuilder,
    private userService: UserService, public dialogRef: MatDialogRef<DialogDeleteUserComponent>,
    public snackBar: MatSnackBar) { }

  ngOnInit() {

  }
  //check if its a robot with captcha
  captcha() {
    if (this.checkedRobot) {
      return true;
    } else {
      return false;
    }
  }
  //delete user
  delete() {
    this.userService.remove(this.data.user.id).subscribe(result => {
      this.dialogRef.close('confirm');
    }, error => this.snackBar.open('There was an error when trying to delete this user.', 'Close', { duration: 3000 }));
  }

}
