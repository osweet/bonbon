/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import ListingDetailComponent from '@/entities/listing/listing-details.vue';
import ListingClass from '@/entities/listing/listing-details.component';
import ListingService from '@/entities/listing/listing.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Listing Management Detail Component', () => {
    let wrapper: Wrapper<ListingClass>;
    let comp: ListingClass;
    let listingServiceStub: SinonStubbedInstance<ListingService>;

    beforeEach(() => {
      listingServiceStub = sinon.createStubInstance<ListingService>(ListingService);

      wrapper = shallowMount<ListingClass>(ListingDetailComponent, {
        store,
        localVue,
        router,
        provide: { listingService: () => listingServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundListing = { id: 123 };
        listingServiceStub.find.resolves(foundListing);

        // WHEN
        comp.retrieveListing(123);
        await comp.$nextTick();

        // THEN
        expect(comp.listing).toBe(foundListing);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundListing = { id: 123 };
        listingServiceStub.find.resolves(foundListing);

        // WHEN
        comp.beforeRouteEnter({ params: { listingId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.listing).toBe(foundListing);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
