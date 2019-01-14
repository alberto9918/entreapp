import { MatSnackBar } from '@angular/material/snack-bar';
import { Component, OnInit } from '@angular/core';
import { UserResponse } from 'src/app/interfaces/user-response';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AuthenticationService} from '../../services/authentication.service';
import { DialogEditUserComponent } from '../../dialogs/dialog-edit-user/dialog-edit-user.component';
@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.scss']
})
export class MyProfileComponent implements OnInit {
  images: any[] = [];
  num = 1;

  pieChartColors: any[] = [{
    backgroundColor: ['#f44336', '#3f51b5', '#ffeb3b', '#4caf50', '#2196f']
  }];

  pieOptions: any = {
    responsive: true,
    legend: {
      position: 'right'
    }
  };

  pieChartLabels: string[] = ['MS Word', 'Typing', 'Sage Pastel'];
  pieChartData: number[] = [300, 500, 100];
  pieChartType = 'pie';

  user: any;

  constructor(private userService: UserService,  private router: Router,
    private authService: AuthenticationService,
    public snackBar: MatSnackBar, public dialog: MatDialog) { }

  ngOnInit() {
    this.getUser();
  }

  getUser() {
    this.userService.getOneByEmail(this.authService.getEmail()).suscribe(user => {
      this.user = user;
    });
  }
  openDialogEditUser(user: UserResponse) {
    const dialogEditUser = this.dialog.open(DialogEditUserComponent, {
      data: {user: user}
    });

    dialogEditUser.afterClosed().subscribe(result => {
      this.getUser();
    });
  }
}
