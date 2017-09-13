#include <cstdio>
#include <cstring>
#include <cstdlib>
using namespace std;

void login1(char * input1, char * input2) {
	struct {
		char username[20];
		char password[30];
		char canary;		
		char good_username[20];
		char good_password[30];
		char goodcanary;
	} v;
	v.canary = 'a';
	v.goodcanary = 'a';

	//read correct username and password
	FILE * fp = fopen("password", "r");
	fgets(v.good_username, 20, fp);
	fgets(v.good_password, 30, fp);
	fclose(fp);
	v.good_username[strlen(v.good_username)-1] = '\0';
	v.good_password[strlen(v.good_password)-1] = '\0';
	strcpy(v.username, input1);
	strcpy(v.password, input2);

	//terminate strings properly
	v.username[19] = '\0';
	v.password[29] = '\0';
	v.good_username[19] = '\0';
	v.good_password[29] = '\0';

	//check login success
	if (v.canary != v.goodcanary) {
		printf("Stack overflow detected, exiting.\n");
		exit(-1);
	}
	if (strcmp(v.username, v.good_username) == 0 && strcmp(v.password, v.good_password) == 0) printf("Login successful!\n");

}

void login2(char * input1, char * input2) {
	struct {
		int goodcanary;
		char username[20];
		char password[30];
		int canary;
		char good_username[20];
		char good_password[30];
	} v;

	//read correct username and password
	FILE * fp = fopen("password", "r");
	fgets(v.good_username, 20, fp);
	fgets(v.good_password, 30, fp);
	fclose(fp);
	v.good_username[strlen(v.good_username)-1] = '\0';
	v.good_password[strlen(v.good_password)-1] = '\0';
	strcpy(v.username, input1);
	strcpy(v.password, input2);

	//set up canary	
	v.goodcanary =  v.username[1]*256*256*257 + (v.username[0]-20)*256 + 67;

	//terminate strings properly
	v.username[19] = '\0';
	v.password[29] = '\0';
	v.good_username[19] = '\0';
	v.good_password[29] = '\0';

	//check login success
	if (v.canary != v.goodcanary) {
		printf("Stack overflow detected, exiting.\n");
		exit(-1);
	}
	if (strcmp(v.username, v.good_username) == 0 && strcmp(v.password, v.good_password) == 0) printf("Login successful!\n");

}

void login3(char * input1, char * input2) {
	struct {
		char username[20];
		char password[30];
		char good_username[20];
		char good_password[30];
		char good;
	} v;
	v.good = -1;

	//read correct username and password
	FILE * fp = fopen("password", "r");
	fgets(v.good_username, 20, fp);
	fgets(v.good_password, 30, fp);
	fclose(fp);
	v.good_username[strlen(v.good_username)-1] = '\0';
	v.good_password[strlen(v.good_password)-1] = '\0';
	strcpy(v.username, input1);
	strcpy(v.password, input2);

	//terminate strings properly
	v.username[19] = '\0';
	v.password[29] = '\0';
	v.good_username[19] = '\0';
	v.good_password[29] = '\0';

	//check login success
	if (strcmp(v.username, v.good_username) == 0 && strcmp(v.password, v.good_password) == 0) v.good = 0;
	if (v.good == 0) printf("Login successful!\n");

}

int main(int argc, char* argv[]) {
	char helpstr[] = "Use: login -? <input file or username password>\nOptions are: -i, -j, -f (see manual)\n";
	if (argc < 3) {
		printf("%s\n", helpstr);
		return -1;
	}

	if (strlen(argv[1]) < 2) {
		printf("%s\n", helpstr);
		return -1;
	}

	switch(argv[1][1]) {
		case 'i':
			if (argc < 4) {
				break;
			}
			login1(argv[2], argv[3]);
			break;
		case 'j':
			if (argc < 4) {
				break;
			}
			login2(argv[2], argv[3]);
			break;
		case 'k':
			if (argc < 4) {
				break;
			}
			login3(argv[2], argv[3]);
			break;
		default:
			printf("%s\n", helpstr);
			return -1;
	}
	return 0;
}
