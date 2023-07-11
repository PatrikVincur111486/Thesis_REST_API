package sk.stuba.fei.uim.vsa.pr1;

import sk.stuba.fei.uim.vsa.pr1.entities.*;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class ThesisService extends AbstractThesisService<Student, Pedagog,ZaverecnaPraca>{
    public ThesisService() {
        super();
    }

    public Object getUserByEmail(String email) {
        if(email==null)    throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Object user;
        try {
            user = getTeacherByEmail(email);
            if (user==null){
                user=getStudentByEmail(email);
                if(user == null){
                    return null;
                }
            }
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return user;
    }

    @Override
    public Student createStudent(Long aisId, String name, String email, String password) {
        EntityManager manager = emf.createEntityManager();
        Student student;
        try{
            student = new Student(aisId, name, email, BCryptService.hash(new String(Base64.getDecoder().decode(password))));
            manager.getTransaction().begin();
            manager.persist(student);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return student;
    }

    @Override
    public Student getStudent(Long id) {
        if(id==null)    throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Student student;
        try {
            student = manager.find(Student.class,id);
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return student;
    }


    public Student getStudentByEmail(String email) {
        if(email==null)    throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Student student;
        try {
            TypedQuery<Student> query = manager.createNamedQuery(Student.FIND_BY_EMAIL, Student.class);
            query.setParameter("email",email);
            student=query.getSingleResult();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return student;
    }

    @Override
    public Student updateStudent(Student student) {
        if(student==null || student.getAisId()==null) throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        if(manager.find(Student.class,student.getAisId())==null) return null;
        Student updatedStudent;
        try {
            manager.getTransaction().begin();
            updatedStudent = manager.merge(student);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return updatedStudent;
    }

    @Override
    public List<Student> getStudents() {
        EntityManager manager = emf.createEntityManager();
        List<Student> resultList;
        try{
            Query query = manager.createNamedQuery(Student.FIND_ALL, Student.class);
            resultList = query.getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }finally {
            manager.close();
        }
        return resultList;
    }

    @Override
    public Student deleteStudent(Long id) {
        if(id == null) throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Student student;
        try{
            manager.getTransaction().begin();
            student=manager.find(Student.class,id);
            if(student.getZaverecnaPraca()!=null)
                student.getZaverecnaPraca().setVypracovatel(null);
            manager.remove(student);
            manager.getTransaction().commit();

        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return student;
    }

    @Override
    public Pedagog createTeacher(Long aisId, String name, String email, String department, String password) {
        EntityManager manager = emf.createEntityManager();
        Pedagog pedagog;
        try{
            pedagog = new Pedagog(aisId, name, email,department, BCryptService.hash(new String(Base64.getDecoder().decode(password))));
            manager.getTransaction().begin();
            manager.persist(pedagog);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return pedagog;
    }

    @Override
    public Pedagog getTeacher(Long id) {
        if(id==null)    throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Pedagog pedagog;
        try {
            pedagog = manager.find(Pedagog.class,id);
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return pedagog;
    }

    public Pedagog getTeacherByEmail(String email) {
        if(email==null)    throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Pedagog pedagog;
        try {
            TypedQuery<Pedagog> query = manager.createNamedQuery(Pedagog.FIND_BY_EMAIL, Pedagog.class);
            query.setParameter("email",email);
            pedagog=query.getSingleResult();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return pedagog;
    }

    @Override
    public Pedagog updateTeacher(Pedagog teacher) {
        if(teacher==null || (teacher.getAisId()==null)) throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Pedagog updatedTeacher;
        try {
            manager.getTransaction().begin();
            updatedTeacher = manager.merge(teacher);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return updatedTeacher;
    }

    @Override
    public List<Pedagog> getTeachers() {
        EntityManager manager = emf.createEntityManager();
        List<Pedagog> resultList=new ArrayList<>();
        try {
            TypedQuery<Pedagog> query= manager.createNamedQuery(Pedagog.FIND_ALL, Pedagog.class);
            resultList = query.getResultList();
        }catch (Exception e){
            return resultList;
        }finally {
            manager.close();
        }
        return resultList;
    }

    @Override
    public Pedagog deleteTeacher(Long id) {
        if(id == null) throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Pedagog pedagog;
        try{
            manager.getTransaction().begin();
            pedagog=manager.find(Pedagog.class,id);
            for(ZaverecnaPraca zaverecnaPraca:pedagog.getVypisanePrace()) {
                if (zaverecnaPraca.getVypracovatel() != null) {
                    zaverecnaPraca.getVypracovatel().setZaverecnaPraca(null);
                }
            }
            manager.remove(pedagog);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return pedagog;
    }

    @Override
    public ZaverecnaPraca makeThesisAssignment(Long supervisor, String title, String type, String description, String regCislo) {
        EntityManager manager = emf.createEntityManager();
        if(supervisor==null)    throw new IllegalArgumentException();
        Pedagog veduciPrace;
        ZaverecnaPraca zaverecnaPraca;
        try{
            veduciPrace= manager.find(Pedagog.class,supervisor);
            zaverecnaPraca=new ZaverecnaPraca(veduciPrace, title, Typ.valueOf(type),description);
            manager.getTransaction().begin();
            veduciPrace.addZaverecnaPraca(zaverecnaPraca);
            zaverecnaPraca.setPracovisko(veduciPrace.getOddelenie());
            Date aktualnyDatum = new Date();
            zaverecnaPraca.setDatumZverejnenia(new Date());
            Date deadline = new Date();
            deadline.setMonth(aktualnyDatum.getMonth()+3);
            zaverecnaPraca.setDeadline(deadline);
            zaverecnaPraca.setVeduciPrace(veduciPrace);
            manager.persist(zaverecnaPraca);
            zaverecnaPraca.setRegistracneCislo(regCislo);
            manager.getTransaction().commit();
//
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return zaverecnaPraca;
    }

    @Override
    public ZaverecnaPraca assignThesis(Long thesisId, Long studentId) {
        if(thesisId==null || studentId==null)    throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        Student student=manager.find(Student.class,studentId);;
        ZaverecnaPraca zaverecnaPraca=manager.find(ZaverecnaPraca.class,thesisId);
        if(student==null){
            return null;
        }
        if(student.getZaverecnaPraca()!=null) return null;
        if(zaverecnaPraca==null) return null;
        if(zaverecnaPraca.getStatus().equals(Status.ZABRANÁ) ||  (zaverecnaPraca.getDeadline().before(new Date())))
            throw new IllegalStateException();
        try{
            manager.getTransaction().begin();
            if(student.getZaverecnaPraca()!=null){
                student.getZaverecnaPraca().setStatus(Status.VOĽNÁ);
            }
            zaverecnaPraca.setStatus(Status.ZABRANÁ);
            student.setZaverecnaPraca(zaverecnaPraca);
            zaverecnaPraca.setVypracovatel(student);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return zaverecnaPraca;
    }

    @Override
    public ZaverecnaPraca submitThesis(Long thesisId) {
        if(thesisId==null)    throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        ZaverecnaPraca zaverecnaPraca=manager.find(ZaverecnaPraca.class,thesisId);
        if(zaverecnaPraca!=null){
            if(zaverecnaPraca.getStatus().equals(Status.ODOVZDANÁ) || zaverecnaPraca.getDeadline().before(new Date()))
                throw new IllegalStateException();
        }
        try{
            manager.getTransaction().begin();
            zaverecnaPraca.setStatus(Status.ODOVZDANÁ);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return zaverecnaPraca;
    }

    @Override
    public ZaverecnaPraca deleteThesis(Long id) {
        if(id == null) throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        ZaverecnaPraca zaverecnaPraca;
        try{
            zaverecnaPraca=manager.find(ZaverecnaPraca.class,id);
            manager.getTransaction().begin();
            if(zaverecnaPraca.getVypracovatel()!=null){
                zaverecnaPraca.getVypracovatel().setZaverecnaPraca(null);
            }
            manager.remove(zaverecnaPraca);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return zaverecnaPraca;
    }

    @Override
    public List<ZaverecnaPraca> getTheses() {
        EntityManager manager = emf.createEntityManager();
        List<ZaverecnaPraca> resultList;
        try{
            TypedQuery<ZaverecnaPraca> query= manager.createNamedQuery(ZaverecnaPraca.FIND_ALL, ZaverecnaPraca.class);
            resultList = query.getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }finally {
            manager.close();
        }
        return resultList;
    }

    @Override
    public List<ZaverecnaPraca> getThesesByTeacher(Long teacherId) {
        EntityManager manager = emf.createEntityManager();
        List<ZaverecnaPraca> resultList;
        try{
            Query query = manager.createNamedQuery(ZaverecnaPraca.FIND_BY_PEDAGOG_ID, Pedagog.class);
            query.setParameter("veduciPraceId",teacherId);
            resultList =query.getResultList();
        }catch (Exception e){
            return new ArrayList<>();
        }finally {
            manager.close();
        }
        return resultList;
    }

    @Override
    public ZaverecnaPraca getThesisByStudent(Long studentId) {
        EntityManager manager = emf.createEntityManager();
        ZaverecnaPraca zaverecnaPraca;
        try{
            TypedQuery<ZaverecnaPraca> query = manager.createNamedQuery(ZaverecnaPraca.FIND_BY_STUDENT_ID, ZaverecnaPraca.class);
            query.setParameter("vypracovatel",manager.find(Student.class,studentId));
            zaverecnaPraca=query.getSingleResult();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return zaverecnaPraca;
    }

    @Override
    public ZaverecnaPraca getThesis(Long id) {
        if(id==null) throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        ZaverecnaPraca zaverecnaPraca;
        try{
            zaverecnaPraca= manager.find(ZaverecnaPraca.class,id);
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return zaverecnaPraca;
    }

    @Override
    public ZaverecnaPraca updateThesis(ZaverecnaPraca thesis) {
        if(thesis==null || thesis.getId()==null) throw new IllegalArgumentException();
        EntityManager manager = emf.createEntityManager();
        ZaverecnaPraca updatedThesis;
        try {
            manager.getTransaction().begin();
            updatedThesis = manager.merge(thesis);
            manager.getTransaction().commit();
        }catch (Exception e){
            return null;
        }finally {
            manager.close();
        }
        return updatedThesis;
    }
}
