import { Component, Vue, Inject } from 'vue-property-decorator';

import { IListing } from '@/shared/model/listing.model';
import ListingService from './listing.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class ListingDetails extends Vue {
  @Inject('listingService') private listingService: () => ListingService;
  @Inject('alertService') private alertService: () => AlertService;

  public listing: IListing = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.listingId) {
        vm.retrieveListing(to.params.listingId);
      }
    });
  }

  public retrieveListing(listingId) {
    this.listingService()
      .find(listingId)
      .then(res => {
        this.listing = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
