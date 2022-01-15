import { Component, Vue, Inject } from 'vue-property-decorator';

import { maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

import { IListing, Listing } from '@/shared/model/listing.model';
import ListingService from './listing.service';

const validations: any = {
  listing: {
    name: {
      maxLength: maxLength(100),
    },
    description: {
      maxLength: maxLength(2000),
    },
    price: {},
    address: {
      maxLength: maxLength(100),
    },
    category: {},
  },
};

@Component({
  validations,
})
export default class ListingUpdate extends Vue {
  @Inject('listingService') private listingService: () => ListingService;
  @Inject('alertService') private alertService: () => AlertService;

  public listing: IListing = new Listing();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.listingId) {
        vm.retrieveListing(to.params.listingId);
      }
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.listing.id) {
      this.listingService()
        .update(this.listing)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Listing is updated with identifier ' + param.id;
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.listingService()
        .create(this.listing)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Listing is created with identifier ' + param.id;
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveListing(listingId): void {
    this.listingService()
      .find(listingId)
      .then(res => {
        this.listing = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
