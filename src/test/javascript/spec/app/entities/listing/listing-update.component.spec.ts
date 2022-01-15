/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import ListingUpdateComponent from '@/entities/listing/listing-update.vue';
import ListingClass from '@/entities/listing/listing-update.component';
import ListingService from '@/entities/listing/listing.service';

import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Listing Management Update Component', () => {
    let wrapper: Wrapper<ListingClass>;
    let comp: ListingClass;
    let listingServiceStub: SinonStubbedInstance<ListingService>;

    beforeEach(() => {
      listingServiceStub = sinon.createStubInstance<ListingService>(ListingService);

      wrapper = shallowMount<ListingClass>(ListingUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          listingService: () => listingServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.listing = entity;
        listingServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(listingServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.listing = entity;
        listingServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(listingServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundListing = { id: 123 };
        listingServiceStub.find.resolves(foundListing);
        listingServiceStub.retrieve.resolves([foundListing]);

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
